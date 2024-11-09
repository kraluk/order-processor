package io.kraluk.orderprocessor.domain.order.entity;

import static org.assertj.core.api.Assertions.assertThat;

import io.kraluk.orderprocessor.test.domain.order.entity.TestOrderBuilder;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class OrderTest {

  @Test
  void shouldCheckIfHasValidValue() {
    // Given
    final var order = TestOrderBuilder.builder().value(Money.of(10, "BGN")).build();

    // When
    boolean result = order.hasIllegalValue();

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void shouldCheckIfHasIllegalValue() {
    // Given
    final var order = TestOrderBuilder.builder().value(Money.of(-10, "HRK")).build();

    // When
    boolean result = order.hasIllegalValue();

    // Then
    assertThat(result).isTrue();
  }
}
