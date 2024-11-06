package io.kraluk.orderprocessor.shared.csv;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.*;
import java.nio.charset.StandardCharsets;

class CsvContentReader implements Closeable {
  private static final char DEFAULT_COLUMN_SEPARATOR = ',';

  private final InputStream content;
  private final char columnSeparator;

  CsvContentReader(final InputStream content, final char columnSeparator) {
    this.content = content;
    this.columnSeparator = columnSeparator;
  }

  CsvContentReader(final InputStream content) {
    this(content, DEFAULT_COLUMN_SEPARATOR);
  }

  <T> MappingIterator<T> read(final Class<T> clazz) {
    try {
      final var reader = new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8));
      return readerOf(clazz).readValues(reader);
    } catch (Exception e) {
      throw new IllegalStateException("Error occurred when reading CSV content", e);
    }
  }

  private <T> ObjectReader readerOf(final Class<T> clazz) {
    return new CsvMapper()
        .enable(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .findAndRegisterModules()
        .readerFor(clazz)
        .with(CsvSchema.emptySchema().withHeader().withColumnSeparator(columnSeparator))
        .with(CsvParser.Feature.TRIM_SPACES);
  }

  @Override
  public void close() throws IOException {
    content.close();
  }
}
