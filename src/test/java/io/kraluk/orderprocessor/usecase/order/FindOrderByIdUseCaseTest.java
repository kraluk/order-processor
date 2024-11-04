package io.kraluk.orderprocessor.usecase.order;

import io.kraluk.orderprocessor.test.adapter.order.repository.InMemoryOrderRepository;
import io.kraluk.orderprocessor.usecase.order.FindOrderByIdUseCase.Command;
import org.junit.jupiter.api.Test;

import static io.kraluk.orderprocessor.domain.order.entity.OrderFixtures.completeOrder;
import static org.assertj.core.api.Assertions.assertThat;

class FindOrderByIdUseCaseTest {

  private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
  private final FindOrderByIdUseCase useCase = new FindOrderByIdUseCase(repository);

  @Test
  void shouldFindById() {
    // Given
    final var order = completeOrder();

    // And given order is saved
    repository.save(order);

    // When
    final var result = useCase.invoke(Command.of(order.getId()));

    // Then
    assertThat(result)
        .isPresent()
        .containsSame(order)
        .usingRecursiveAssertion();
  }

  @Test
  void shouldNotFindById() {
    // Given
    // empty repository

    // When
    final var result = useCase.invoke(Command.of(1L));

    // Then
    assertThat(result)
        .isNotPresent();
  }
}