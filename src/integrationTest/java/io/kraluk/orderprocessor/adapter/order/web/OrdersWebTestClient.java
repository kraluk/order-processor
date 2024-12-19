package io.kraluk.orderprocessor.adapter.order.web;

import static java.util.Objects.requireNonNull;

import java.util.UUID;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;

public class OrdersWebTestClient {

  private final RestClient client;

  public OrdersWebTestClient(final RestClient client) {
    this.client = requireNonNull(client);
  }

  public <T> ResponseEntity<T> getById(final Long id, final Class<T> responseType) {
    return client
        .get()
        .uri(builder -> builder.path("/v1/orders").pathSegment("{id}").build(id))
        .retrieve()
        .onStatus(HttpStatusCode::isError, ignore)
        .toEntity(responseType);
  }

  public <T> ResponseEntity<T> getByBusinessId(final UUID businessId, final Class<T> responseType) {
    return client
        .get()
        .uri(builder ->
            builder.path("/v1/orders").queryParam("businessId", businessId).build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, ignore)
        .toEntity(responseType);
  }

  // used to not throwing exceptions on error status codes
  private static final ErrorHandler ignore = (request, response) -> {};
}
