package io.kraluk.orderprocessor.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;

import io.kraluk.orderprocessor.test.adapter.order.repository.InMemoryOrderRepository;
import io.kraluk.orderprocessor.test.domain.order.entity.TestOrderBuilder;
import io.kraluk.orderprocessor.usecase.order.FindOrderByBusinessIdUseCase.Command;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FindOrderByBusinessIdUseCaseTest {

  private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
  private final FindOrderByBusinessIdUseCase useCase = new FindOrderByBusinessIdUseCase(repository);

  @Test
  void shouldFindByBusinessId() {
    // Given
    final var order = TestOrderBuilder.buildRandom();

    // And given order is saved
    repository.save(order);

    // When
    final var result = useCase.invoke(Command.of(order.getBusinessId()));

    // Then
    assertThat(result).isPresent().containsSame(order).usingRecursiveAssertion();
  }

  @Test
  void shouldNotFindByBusinessIdWhenOrderDoesNotExist() {
    // Given
    // empty repository

    // When
    final var result = useCase.invoke(Command.of(UUID.randomUUID()));

    // Then
    assertThat(result).isNotPresent();
  }
}
