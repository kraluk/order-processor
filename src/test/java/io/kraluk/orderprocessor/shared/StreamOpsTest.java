package io.kraluk.orderprocessor.shared;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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

  @Test
  void shouldConvertIteratorToStream() {
    // Given
    final var iterator = Arrays.asList(1, 2, 3, 4).iterator();

    // When
    final var stream = StreamOps.toStream(iterator);

    // Then
    assertThat(stream.toList()).containsExactly(1, 2, 3, 4);
  }
}
