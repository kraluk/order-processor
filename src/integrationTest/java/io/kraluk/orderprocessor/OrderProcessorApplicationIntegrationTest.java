package io.kraluk.orderprocessor;

import io.kraluk.orderprocessor.test.AwsIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class OrderProcessorApplicationIntegrationTest extends AwsIntegrationTest {

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
