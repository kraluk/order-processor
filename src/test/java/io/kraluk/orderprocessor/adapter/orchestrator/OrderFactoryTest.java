package io.kraluk.orderprocessor.adapter.orchestrator;

import io.kraluk.orderprocessor.test.domain.orderupdate.entity.TestOrderUpdateBuilder;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static io.kraluk.orderprocessor.test.TestClockOps.ARBITRARY_NOW;
import static io.kraluk.orderprocessor.test.TestClockOps.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;

class OrderFactoryTest {

  private final Clock clock = fixedClock();
  private final OrderFactory factory = new OrderFactory(clock);

  @Test
  void shouldCreateOrderFromUpdate() {
    // Given
    final var update = TestOrderUpdateBuilder.buildRandom();

    // When
    final var order = factory.from(update);

    // Then
    assertThat(order)
        .isNotNull()
        .matches(o -> o.getBusinessId().equals(update.getBusinessId()))
        .matches(o -> o.getValue().equals(Money.of(update.getValue(), update.getCurrency())))
        .matches(o -> o.getNotes().equals(update.getNotes()))
        .matches(o -> o.getUpdatedAt().equals(update.getUpdatedAt()))
        .matches(o -> o.getReadAt().equals(ARBITRARY_NOW))
        .matches(o -> o.getId() == null)
        .matches(o -> o.getCreatedAt() == null);
  }
}