package io.kraluk.orderprocessor.usecase.order;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.order.port.OrderRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class FindOrderByIdUseCase {

  private final OrderRepository repository;

  public FindOrderByIdUseCase(OrderRepository repository) {
    this.repository = repository;
  }

  public Optional<Order> invoke(Command command) {
    return repository.findById(command.id());
  }

  public record Command(Long id) {
    public static Command of(final Long id) {
      return new Command(id);
    }
  }
}
