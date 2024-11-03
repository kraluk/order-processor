package io.kraluk.batchprocessor.shared;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamOps {

  private StreamOps() {
    throw new UnsupportedOperationException("This class should not be instantiated");
  }

  public static <T> Stream<List<T>> fixedWindow(Stream<T> stream, int size) {
    final Iterator<T> iterator = stream.iterator();
    final Iterator<List<T>> listIterator =
        new Iterator<>() {

          public boolean hasNext() {
            return iterator.hasNext();
          }

          public List<T> next() {
            final List<T> result = new ArrayList<>(size);

            for (int i = 0; i < size && iterator.hasNext(); i++) {
              result.add(iterator.next());
            }

            return result;
          }
        };
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(listIterator, Spliterator.ORDERED), false);
  }
}
