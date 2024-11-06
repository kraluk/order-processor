package io.kraluk.orderprocessor.configuration;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ClockConfigurationTest {

  private final ClockConfiguration configuration = new ClockConfiguration();

  @Test
  void shouldCreateClockWithUtcTimeZone() {
    // When
    final var clock = configuration.clock();

    // Then
    assertThat(clock.getZone())
        .isEqualTo(ZoneId.of("Z"));
  }
}