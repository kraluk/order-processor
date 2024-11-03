package io.kraluk.batchprocessor.shared;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamOps {

  private StreamOps() {
    throw new UnsupportedOperationException("This class should not be instantiated!");
  }

  public static <T> Stream<List<T>> fixedWindow(Stream<T> stream, int windowSize) {
    final Iterator<T> streamIterator = stream.iterator();
    final Iterator<List<T>> windowIterator =
        new FixedWindowStreamIterator<>(streamIterator, windowSize);

    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(windowIterator, Spliterator.ORDERED), false);
  }

  private static class FixedWindowStreamIterator<T> implements Iterator<List<T>> {

    private final Iterator<T> streamIterator;
    private final int windowSize;

    public FixedWindowStreamIterator(Iterator<T> streamIterator, int windowSize) {
      this.streamIterator = streamIterator;
      this.windowSize = windowSize;
    }

    @Override
    public boolean hasNext() {
      return streamIterator.hasNext();
    }

    @Override
    public List<T> next() {
      final List<T> result = new ArrayList<>(windowSize);

      for (int i = 0; i < windowSize && streamIterator.hasNext(); i++) {
        result.add(streamIterator.next());
      }

      return result;
    }
  }
}
