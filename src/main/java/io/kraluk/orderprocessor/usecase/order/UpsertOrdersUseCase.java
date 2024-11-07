package io.kraluk.orderprocessor.usecase.order;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.order.port.OrderRepository;
import io.kraluk.orderprocessor.domain.order.port.OrderTemporaryRepository;
import io.kraluk.orderprocessor.domain.shared.SessionId;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class UpsertOrdersUseCase {

  private final OrderRepository repository;
  private final OrderTemporaryRepository temporaryRepository;

  public UpsertOrdersUseCase(
      final OrderRepository repository, final OrderTemporaryRepository temporaryRepository) {
    this.repository = repository;
    this.temporaryRepository = temporaryRepository;
  }

  public Stream<Order> invoke(final Command command) {
    final var sessionId = SessionId.random();
    final var temporaryTable = temporaryRepository.createTemporaryTable(sessionId);

    temporaryRepository.saveInto(command.orders, temporaryTable);
    return repository.upsertFromTempTable(temporaryTable);
  }

  public record Command(Stream<Order> orders) {
    public static Command of(final Stream<Order> orders) {
      return new Command(orders);
    }
  }
}
