package io.kraluk.orderprocessor.adapter.order.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class OrdersWebTestClient {

  private final RestClient client;

  public OrdersWebTestClient(final RestClient client) {
    this.client = requireNonNull(client);
  }

  public <T> ResponseEntity<T> getById(final Long id, final Class<T> responseType) {
    return client.get()
        .uri(builder -> builder
            .path("/v1/orders")
            .pathSegment("{id}")
            .build(id))
        .retrieve()
        .toEntity(responseType);
  }

  public <T> ResponseEntity<T> getByBusinessId(final UUID businessId, final Class<T> responseType) {
    return client.get()
        .uri(builder -> builder
            .path("/v1/orders")
            .queryParam("businessId", businessId)
            .build())
        .retrieve()
        .toEntity(responseType);
  }
}
