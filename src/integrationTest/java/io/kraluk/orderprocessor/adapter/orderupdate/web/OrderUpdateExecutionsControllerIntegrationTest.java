package io.kraluk.orderprocessor.adapter.orderupdate.web;

import io.kraluk.orderprocessor.shared.contract.http.OrderUpdateExecutionHttp;
import io.kraluk.orderprocessor.test.InMemoryTestConfiguration;
import io.kraluk.orderprocessor.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Import(InMemoryTestConfiguration.class) // just to not test the whole orchestration machinery in this particular test
class OrderUpdateExecutionsControllerIntegrationTest extends IntegrationTest {

  @Autowired
  private RestClient testRestClient;

  @Test
  void shouldInitiateOrderUpdateExecution() {
    // Given
    final var source = "test-source";

    // When
    final var result = testClient().executeProcess(source, OrderUpdateExecutionHttp.class);

    // Then
    assertThat(result)
        .isNotNull()
        .matches(d -> d.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(202)))
        .extracting(HttpEntity::getBody)
        .matches(b -> Objects.equals(b.source(), source))
        .matches(b -> Objects.equals(b.message(), "Order update process has been accepted."))
        .matches(b -> b.timestamp() != null);
  }

  protected OrderUpdateExecutionsWebTestClient testClient() {
    return new OrderUpdateExecutionsWebTestClient(testRestClient);
  }
}