package io.kraluk.orderprocessor.shared;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class StreamOpsTest {

  @Test
  void shouldWindowGivenStream() {
    // Given
    final var input = Stream.of(1, 2, 3, 4, 5);

    // When
    final var result = StreamOps.fixedWindow(input, 2);

    // Then
    final var mapped = result.toList();

    assertThat(mapped).containsExactly(List.of(1, 2), List.of(3, 4), List.of(5));
  }
}
