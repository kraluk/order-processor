package io.kraluk.orderprocessor.shared.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;

public class CsvContentSource<T> implements Closeable, Iterator<T> {
  private static final Logger log = LoggerFactory.getLogger(CsvContentSource.class);

  private final CsvContentReader csvSource;
  private final MappingIterator<T> iterator;

  public CsvContentSource(final Class<T> clazz, final InputStream content) {
    this.csvSource = new CsvContentReader(content);
    this.iterator = csvSource.read(clazz);
  }

  @Override
  public void close() {
    try {
      csvSource.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public boolean hasNext() {
    try {
      return iterator.hasNextValue();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public T next() {
    try {
      return iterator.next();
    } catch (Exception e) {
      // FEATURE: better error handling, maybe using vavr's `Either`?
      log.error("Error while reading the next value of the CSV source!", e);
      return null;
    }
  }
}
