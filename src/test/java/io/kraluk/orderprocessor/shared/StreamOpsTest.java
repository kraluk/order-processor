package io.kraluk.orderprocessor.shared;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class StreamOpsTest {

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
