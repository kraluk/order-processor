package io.kraluk.batchprocessor;

import io.kraluk.batchprocessor.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class BatchProcessorApplicationIntegrationTest extends IntegrationTest {

  @Autowired
  private Environment environment;

  @Test
  void shouldLoadContext() {
    // When
    var profiles = Arrays.stream(environment.getActiveProfiles()).toList();

    // Then
    assertThat(profiles).contains("integration");
  }
}
