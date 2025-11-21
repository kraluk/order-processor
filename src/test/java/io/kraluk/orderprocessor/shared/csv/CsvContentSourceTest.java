package io.kraluk.orderprocessor.shared.csv;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class CsvContentSourceTest {

  @Test
  void shouldReadValidData() {
    // Given
    final var input = """
        "id","name","price"
        "1","Product 1","100.00"
        "2","Product 2","200.00"
        """.trim();

    final var content = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

    // When
    final var source = new CsvContentSource<>(TestCsvRecord.class, content);

    // Then
    assertThat(source.next()).isEqualTo(TestCsvRecord.of(1, "Product 1", new BigDecimal("100.00")));
    assertThat(source.next()).isEqualTo(TestCsvRecord.of(2, "Product 2", new BigDecimal("200.00")));
    assertThat(source.hasNext()).isFalse();

    // And then close the source calmly
    source.close();
  }

  @Test
  void shouldIgnoreAndNullifyBadlyFormattedEntriesAndReadOnlyValidData() {
    // Given
    final var input = """
        "id","name","price"
        "1","Product 1","100.00"
        "XXX","Product 2","XXX"
        """.trim();

    final var content = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

    // When
    final var source = new CsvContentSource<>(TestCsvRecord.class, content);

    // Then
    assertThat(source.next()).isEqualTo(TestCsvRecord.of(1, "Product 1", new BigDecimal("100.00")));
    assertThat(source.next()).isNull();
    assertThat(source.hasNext()).isFalse();

    // And then close the source calmly
    source.close();
  }

  private record TestCsvRecord(int id, String name, BigDecimal price) {
    static TestCsvRecord of(int id, String name, BigDecimal price) {
      return new TestCsvRecord(id, name, price);
    }
  }
}
