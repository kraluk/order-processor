package io.kraluk.orderprocessor.adapter.orderupdate.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static java.util.Objects.requireNonNull;

class OrderUpdateExecutionsWebTestClient {

  private final RestClient client;

  OrderUpdateExecutionsWebTestClient(final RestClient client) {
    this.client = requireNonNull(client);
  }

  <T> ResponseEntity<T> executeProcess(final String source, final Class<T> responseType) {
    return client.post()
        .uri(builder -> builder
            .path("/v1/orders/update-invocations")
            .pathSegment("{source}")
            .build(source))
        .retrieve()
        .toEntity(responseType);
  }
}
