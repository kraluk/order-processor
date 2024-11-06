package io.kraluk.orderprocessor.domain.orderupdate.entity;

import java.io.InputStream;

public record OrderUpdateContent(String fileName, InputStream stream) {
  public static OrderUpdateContent of(final String fileName, final InputStream stream) {
    return new OrderUpdateContent(fileName, stream);
  }
}