package io.kraluk.orderprocessor.test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

public final class TestClockOps {
  public static final ZoneId DEFAULT_TIME_ZONE = ZoneOffset.UTC;
  public static final Instant ARBITRARY_NOW = Instant.parse("2024-11-06T18:00:00.001Z");

  private TestClockOps() {
    throw new UnsupportedOperationException("This class should not be instantiated");
  }

  public static Clock fixedClock() {
    return Clock.fixed(ARBITRARY_NOW, DEFAULT_TIME_ZONE);
  }
}
