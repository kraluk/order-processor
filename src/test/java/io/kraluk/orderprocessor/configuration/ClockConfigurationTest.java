package io.kraluk.orderprocessor.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ClockConfigurationTest {

  private final ClockConfiguration configuration = new ClockConfiguration();

  @Test
  void shouldCreateClockWithUtcTimeZone() {
    // When
    final var clock = configuration.clock();

    // Then
    assertThat(clock.getZone()).isEqualTo(ZoneOffset.UTC);
  }
}
