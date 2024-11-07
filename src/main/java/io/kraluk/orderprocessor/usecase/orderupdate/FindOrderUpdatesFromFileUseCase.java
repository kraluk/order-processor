package io.kraluk.orderprocessor.usecase.orderupdate;

import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdate;
import io.kraluk.orderprocessor.domain.orderupdate.port.OrderUpdateRepository;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class FindOrderUpdatesFromFileUseCase {

  private final OrderUpdateRepository repository;

  public FindOrderUpdatesFromFileUseCase(final OrderUpdateRepository repository) {
    this.repository = repository;
  }

  public Stream<OrderUpdate> invoke(final Command command) {
    return repository.findAllFrom(command.source);
  }

  public record Command(String source) {
    public static Command of(String source) {
      return new Command(source);
    }
  }
}
