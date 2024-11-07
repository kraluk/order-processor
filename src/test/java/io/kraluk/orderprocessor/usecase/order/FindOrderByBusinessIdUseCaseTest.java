package io.kraluk.orderprocessor.usecase.order;

import io.kraluk.orderprocessor.test.adapter.order.repository.InMemoryOrderRepository;
import io.kraluk.orderprocessor.test.domain.order.entity.TestOrderBuilder;
import io.kraluk.orderprocessor.usecase.order.FindOrderByBusinessIdUseCase.Command;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
    assertThat(result)
        .isPresent()
        .containsSame(order)
        .usingRecursiveAssertion();
  }

  @Test
  void shouldNotFindByBusinessIdWhenOrderDoesNotExist() {
    // Given
    // empty repository

    // When
    final var result = useCase.invoke(Command.of(UUID.randomUUID()));

    // Then
    assertThat(result)
        .isNotPresent();
  }
}