package io.kraluk.orderprocessor.domain.orderupdate;

import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public final class OrderUpdateFixtures {
  private static final Random RANDOM = new Random();

  private OrderUpdateFixtures() {
    throw new UnsupportedOperationException("This class should not be instantiated");
  }

  public static OrderUpdate completeOrderUpdate() {
    return new OrderUpdate(
        UUID.randomUUID(),
        BigDecimal.valueOf(RANDOM.nextLong(1_000_000)),
        "PLN",
        "some notes",
        Instant.parse("2024-11-05T18:00:00.000Z")
    );
  }
}
