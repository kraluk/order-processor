package io.kraluk.orderprocessor.adapter.orchestrator;

import io.kraluk.orderprocessor.adapter.order.outbox.OrderTransactionOutbox;
import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdate;
import io.kraluk.orderprocessor.shared.StreamOps;
import io.kraluk.orderprocessor.usecase.order.UpsertOrdersUseCase;
import io.kraluk.orderprocessor.usecase.orderupdate.FindOrderUpdatesFromFileUseCase;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Component
public class OrderUpdatesOrchestrator {
  private static final Logger log = LoggerFactory.getLogger(OrderUpdatesOrchestrator.class);

  private final FindOrderUpdatesFromFileUseCase findUseCase;
  private final UpsertOrdersUseCase upsertUseCase;
  private final OrderFactory factory;
  private final OrderTransactionOutbox outbox;
  private final TransactionTemplate transaction;
  private final SimpleAsyncTaskExecutor taskExecutor;
  private final OrderUpdatesOrchestratorProperties properties;

  OrderUpdatesOrchestrator(
      final FindOrderUpdatesFromFileUseCase findUseCase,
      final UpsertOrdersUseCase upsertUseCase,
      final OrderFactory factory,
      final OrderTransactionOutbox outbox,
      final TransactionTemplate transaction,
      final SimpleAsyncTaskExecutor taskExecutor,
      final OrderUpdatesOrchestratorProperties properties) {
    this.findUseCase = findUseCase;
    this.upsertUseCase = upsertUseCase;
    this.factory = factory;
    this.outbox = outbox;
    this.transaction = transaction;
    this.taskExecutor = taskExecutor;
    this.properties = properties;
  }

  // FEATURE: add metrics
  @Async
  public void process(final String source) {
    log.info("Attempting to start processing Order Updates from '{}'", source);

    try (final var updates = findUseCase.invoke(FindOrderUpdatesFromFileUseCase.Command.of(source))) {
      final var tasks = processInChunks(updates);
      waitForAll(tasks);
      final var result = finalize(tasks);

      log.info("Processed successfully '{}' Order Updates", result);
    }
  }

  private List<CompletableFuture<Long>> processInChunks(final Stream<OrderUpdate> updates) {
    return StreamOps.fixedWindow(
            updates.map(factory::from),
            properties.chunkSize())
        .map(this::processChunk)
        .map(t -> t.exceptionally(OrderUpdatesOrchestrator::exceptionally)) // dummy error handling
        .toList();
  }

  private CompletableFuture<Long> processChunk(final List<Order> orders) {
    return taskExecutor
        .submitCompletable(() -> transaction
            .execute(status -> executeChunk(orders)));
  }

  private long executeChunk(final List<Order> orders) {
    log.info("Processing chunk of '{}' Order Updates", orders.size());

    try (final var updated = upsertUseCase.invoke(UpsertOrdersUseCase.Command.of(orders.stream()))) {
      final var result = updated
          .peek(outbox::add)
          .map(o -> 1L)
          .reduce(0L, Long::sum);

      log.info("Processed '{}' Order Updates from initial chunk of '{}'", result, orders.size());
      return result;
    }
  }

  private static void waitForAll(final List<CompletableFuture<Long>> tasks) {
    CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new)).join();
  }

  private static long finalize(final List<CompletableFuture<Long>> tasks) {
    return tasks.stream()
        .map(CompletableFuture::join)
        .reduce(0L, Long::sum);
  }

  // FEATURE: better error handling
  private static Long exceptionally(final Throwable e) {
    log.error("Error while processing chunk of Order Updates", e);
    return 0L;
  }
}

@Component
class OrderFactory {
  private final Clock clock;

  OrderFactory(final Clock clock) {
    this.clock = clock;
  }

  Order from(final OrderUpdate update) {
    // FEATURE: potentially, depending on the requirements, we could firstly get Order from the database,
    // then this instance update with the data from the data - it will cause that we will have complete Order in place, including data that
    // is not available in the update object
    return Order.fromUpdate(
        update.getBusinessId(),
        Money.of(update.getValue(), update.getCurrency()),
        update.getNotes(),
        update.getUpdatedAt(),
        clock.instant()
    );
  }
}

@ConfigurationProperties(prefix = "app.order.orchestrator")
record OrderUpdatesOrchestratorProperties(int chunkSize) {
}
