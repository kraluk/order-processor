package io.kraluk.orderprocessor.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;

import io.kraluk.orderprocessor.test.adapter.order.repository.InMemoryOrderRepository;
import io.kraluk.orderprocessor.test.domain.order.entity.TestOrderBuilder;
import io.kraluk.orderprocessor.usecase.order.FindOrderByIdUseCase.Command;
import org.junit.jupiter.api.Test;

class FindOrderByIdUseCaseTest {

  private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
  private final FindOrderByIdUseCase useCase = new FindOrderByIdUseCase(repository);

  @Test
  void shouldFindById() {
    // Given
    final var order = TestOrderBuilder.buildRandom();

    // And given order is saved
    repository.save(order);

    // When
    final var result = useCase.invoke(Command.of(order.getId()));

    // Then
    assertThat(result).isPresent().containsSame(order).usingRecursiveAssertion();
  }

  @Test
  void shouldNotFindById() {
    // Given
    // empty repository

    // When
    final var result = useCase.invoke(Command.of(1L));

    // Then
    assertThat(result).isNotPresent();
  }
}
