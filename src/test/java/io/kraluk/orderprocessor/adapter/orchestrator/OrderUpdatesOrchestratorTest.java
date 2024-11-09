package io.kraluk.orderprocessor.adapter.orchestrator;

import static io.kraluk.orderprocessor.test.TestClockOps.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.test.NoOpTransactionManager;
import io.kraluk.orderprocessor.test.adapter.order.outbox.InMemoryOrderTransactionOutbox;
import io.kraluk.orderprocessor.test.adapter.order.repository.InMemoryOrderRepository;
import io.kraluk.orderprocessor.test.adapter.order.repository.InMemoryOrderTemporaryRepository;
import io.kraluk.orderprocessor.test.adapter.orderupdate.repository.InMemoryOrderUpdateRepository;
import io.kraluk.orderprocessor.test.domain.orderupdate.entity.TestOrderUpdateBuilder;
import io.kraluk.orderprocessor.usecase.order.UpsertOrdersUseCase;
import io.kraluk.orderprocessor.usecase.orderupdate.FindOrderUpdatesFromFileUseCase;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.support.TransactionTemplate;

class OrderUpdatesOrchestratorTest {

  private final InMemoryOrderUpdateRepository updateRepository =
      new InMemoryOrderUpdateRepository();
  private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
  private final InMemoryOrderTemporaryRepository temporaryRepository =
      new InMemoryOrderTemporaryRepository();
  private final InMemoryOrderTransactionOutbox outbox = new InMemoryOrderTransactionOutbox();

  private final OrderUpdatesOrchestrator orchestrator = orchestrator();

  @Test
  void shouldProcessUpdatesCreatingBrandNewOrdersFromGivenSource() {
    // Given
    final var source = "update_source_file.csv";

    final var update1 = TestOrderUpdateBuilder.buildRandom();
    final var update2 = TestOrderUpdateBuilder.buildRandom();
    final var update3 = TestOrderUpdateBuilder.buildRandom();
    final var update4 = TestOrderUpdateBuilder.buildRandom();
    final var update5 = TestOrderUpdateBuilder.buildRandom();

    final var expectedBusinessIds = List.of(
        update1.getBusinessId(),
        update2.getBusinessId(),
        update3.getBusinessId(),
        update4.getBusinessId(),
        update5.getBusinessId());

    // And given updates are available in the update repository
    updateRepository.save(source, List.of(update1, update2, update3, update4, update5));

    // When
    orchestrator.process(source);

    // Then orders are saved
    assertThat(repository.elements())
        .hasSize(5)
        .extracting(Order::getBusinessId)
        .containsAll(expectedBusinessIds);

    // And then orders are added to the outbox
    assertThat(outbox.elements())
        .hasSize(5)
        .extracting(Order::getBusinessId)
        .containsAll(expectedBusinessIds);
  }

  @Disabled("Due to the time it takes to run this test, it is disabled by default")
  @RepeatedTest(10_000)
  void shouldProcessUpdatesCreatingBrandNewOrdersFromGivenSourceManyManyTimes() {
    shouldProcessUpdatesCreatingBrandNewOrdersFromGivenSource();
  }

  private OrderUpdatesOrchestrator orchestrator() {
    return new OrderUpdatesOrchestrator(
        new FindOrderUpdatesFromFileUseCase(updateRepository),
        new UpsertOrdersUseCase(repository, temporaryRepository),
        new OrderFactory(fixedClock()),
        outbox,
        new OrderMetrics(new SimpleMeterRegistry()),
        new TransactionTemplate(new NoOpTransactionManager()),
        new SimpleAsyncTaskExecutor("tests-"),
        new OrderUpdatesOrchestratorProperties(2));
  }
}
