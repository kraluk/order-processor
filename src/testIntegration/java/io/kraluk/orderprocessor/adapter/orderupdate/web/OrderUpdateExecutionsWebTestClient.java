package io.kraluk.orderprocessor.adapter.orderupdate.web;

import static java.util.Objects.requireNonNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

public final class OrderUpdateExecutionsWebTestClient {

  private final RestClient client;

  public OrderUpdateExecutionsWebTestClient(final RestClient client) {
    this.client = requireNonNull(client);
  }

  public <T> ResponseEntity<T> executeProcess(final String source, final Class<T> responseType) {
    return client
        .post()
        .uri(builder ->
            builder.path("/v1/orders/update-executions").pathSegment("{source}").build(source))
        .retrieve()
        .toEntity(responseType);
  }
}
