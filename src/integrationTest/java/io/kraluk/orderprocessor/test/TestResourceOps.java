package io.kraluk.orderprocessor.test;

import java.nio.file.Path;
import java.util.Objects;

public final class TestResourceOps {

  // getClass().getClassLoader().getResource("orderupdate/s3/new_orders.csv")
  public static Path pathTo(final String resource) {
    return Path.of(
        Objects.requireNonNull(
            TestResourceOps.class.getClassLoader().getResource(resource)).getPath()
    );
  }
}
