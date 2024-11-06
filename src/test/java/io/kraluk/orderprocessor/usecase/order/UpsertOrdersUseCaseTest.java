package io.kraluk.orderprocessor.usecase.order;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.test.adapter.order.repository.InMemoryOrderRepository;
import io.kraluk.orderprocessor.test.adapter.order.repository.InMemoryOrderTemporaryRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static io.kraluk.orderprocessor.domain.order.entity.OrderFixtures.orderWithBusinessId;
import static org.assertj.core.api.Assertions.assertThat;

class UpsertOrdersUseCaseTest {

  private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
  private final InMemoryOrderTemporaryRepository temporaryRepository = new InMemoryOrderTemporaryRepository();

  private final UpsertOrdersUseCase useCase = new UpsertOrdersUseCase(repository, temporaryRepository);

  @Test
  void shouldUpsertGivenOrders() {
    // Given
    final var order1 = orderWithBusinessId(UUID.randomUUID(), BigDecimal.valueOf(1));
    final var order2 = orderWithBusinessId(UUID.randomUUID(), BigDecimal.valueOf(2));

    // When
    final var result = useCase.invoke(UpsertOrdersUseCase.Command.of(Stream.of(order1, order2))).toList();

    // Then
    assertThat(result)
        .hasSize(2)
        .extracting(Order::getBusinessId)
        .containsExactlyInAnyOrder(order1.getBusinessId(), order2.getBusinessId());

    // And then given orders are persisted via the repository
    final var saved = repository.elements();

    assertThat(saved)
        .hasSize(2)
        .extracting(Order::getBusinessId)
        .containsExactlyInAnyOrder(order1.getBusinessId(), order2.getBusinessId());
  }
}