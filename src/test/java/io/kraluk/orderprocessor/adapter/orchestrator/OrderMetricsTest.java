package io.kraluk.orderprocessor.adapter.orchestrator;

import static org.assertj.core.api.Assertions.assertThat;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.test.domain.order.entity.TestOrderBuilder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.math.BigDecimal;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class OrderMetricsTest {

  private final MeterRegistry registry = new SimpleMeterRegistry();

  private final OrderMetrics metrics = new OrderMetrics(registry);

  @Test
  void shouldMeasureOrder() {
    // Given
    final Order order = TestOrderBuilder.builder()
        .value(Money.of(BigDecimal.valueOf(100), "BGN"))
        .build();

    // When
    metrics.measure(order);

    // Then processed count has been increased
    final var processedCount = registry.counter("order_updates_processed").count();
    assertThat(processedCount).isEqualTo(1);

    // And then illegal count has not been increased
    final var illegalCount = registry.counter("order_updates_illegal").count();
    assertThat(illegalCount).isZero();
  }

  @Test
  void shouldMeasureOrderWithIllegalValue() {
    // Given
    final Order order =
        TestOrderBuilder.builder().value(Money.of(BigDecimal.ZERO, "HRK")).build();

    // When
    metrics.measure(order);

    // Then processed count has been increased
    final var processedCount = registry.counter("order_updates_processed").count();
    assertThat(processedCount).isEqualTo(1);

    // And then illegal count has not been increased
    final var illegalCount = registry.counter("order_updates_illegal").count();
    assertThat(illegalCount).isEqualTo(1);
  }

  @Test
  void shouldMeasureCompletionOfOrderUpdateProcess() {
    // Given
    final var count = 1000L;
    final var source = "orders.csv";

    // When
    metrics.measureCompletionOf(source, count);

    // Then processed count has been increased
    final var processedCount = registry
        .counter("order_updates_processed_complete", "source", "orders.csv")
        .count();
    assertThat(processedCount).isEqualTo(1000.00);
  }
}
