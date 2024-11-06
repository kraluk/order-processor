package io.kraluk.orderprocessor.domain.order.entity;

import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public final class OrderFixtures {
  private static final Random RANDOM = new Random();

  private OrderFixtures() {
    throw new UnsupportedOperationException("This class should not be instantiated");
  }

  public static Order completeOrder() {
    return new Order(
        RANDOM.nextLong(1_000_000_000),
        UUID.randomUUID(),
        Money.of(
            BigDecimal.valueOf(RANDOM.nextDouble(1_000)),
            "PLN"
        ),
        null,
        1L,
        Instant.parse("2024-01-01T00:00:00Z"),
        Instant.parse("2024-06-01T00:00:00Z"),
        Instant.parse("2024-11-04T00:00:00Z")
    );
  }

  public static Order orderWithBusinessId(final UUID businessId, final BigDecimal value) {
    return new Order(
        null,
        businessId,
        Money.of(
            value,
            "PLN"
        ),
        null,
        1L,
        Instant.parse("2024-01-01T00:00:00Z"),
        Instant.parse("2024-06-02T00:00:00Z"),
        Instant.parse("2024-11-04T00:00:00Z")
    );
  }
}
