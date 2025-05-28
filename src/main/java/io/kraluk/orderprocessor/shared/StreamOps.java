package io.kraluk.orderprocessor.shared;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class StreamOps {

  private StreamOps() {
    throw new UnsupportedOperationException("This class should not be instantiated!");
  }

  public static <T> Stream<T> toStream(final Iterator<T> iterator) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
  }
}
