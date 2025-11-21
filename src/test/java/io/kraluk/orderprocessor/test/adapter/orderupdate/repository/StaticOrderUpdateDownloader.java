package io.kraluk.orderprocessor.test.adapter.orderupdate.repository;

import io.kraluk.orderprocessor.adapter.orderupdate.repository.OrderUpdateDownloader;
import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdateContent;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class StaticOrderUpdateDownloader implements OrderUpdateDownloader {

  private static final String STATIC_CONTENT = """
        "businessId","value","currency","notes","updatedAt"
        "16eb25dd-a5ee-4e16-b7de-9fb3d4d94e10","100.00","PLN","note1","2024-01-01T00:00:00.001Z"
        "26eb25dd-a5ee-4e16-b7de-9fb3d4d94e11","200.00","CHF","note2","2024-01-02T00:00:00.001Z"
        "36eb25dd-a5ee-4e16-b7de-9fb3d4d94e12","300.00","EUR","note3","2024-01-03T00:00:00.001Z"
        "46eb25dd-a5ee-4e16-b7de-9fb3d4d94e13","400.00","BGN","note4","2024-01-04T00:00:00.001Z"
      """.trim();

  @Override
  public Optional<OrderUpdateContent> download(String source) {
    return Optional.of(new OrderUpdateContent(
        source, new ByteArrayInputStream(STATIC_CONTENT.getBytes(StandardCharsets.UTF_8))));
  }
}
