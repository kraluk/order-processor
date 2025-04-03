package io.kraluk.orderprocessor.test;

import java.nio.file.Path;
import java.util.Objects;

public final class TestOps {

  private TestOps() {
    throw new UnsupportedOperationException("This class should not be instantiated");
  }

  public static Path pathTo(final String resource) {
    return Path.of(
        Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(resource))
            .getPath());
  }
}
