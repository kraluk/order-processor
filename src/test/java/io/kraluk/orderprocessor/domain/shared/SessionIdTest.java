package io.kraluk.orderprocessor.domain.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class SessionIdTest {

  @Test
  void shouldCreateRandomId() {
    // When
    final var result = SessionId.random();

    // Then
    assertThat(result)
        .isNotNull()
        .matches(not(id -> id.value().isBlank()));
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", " ", "  "})
  void shouldNotCreateNullOrBlankId(final String value) {
    // When & Then
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new SessionId(value))
        .withMessage("SessionId cannot be null or blank!");
  }
}