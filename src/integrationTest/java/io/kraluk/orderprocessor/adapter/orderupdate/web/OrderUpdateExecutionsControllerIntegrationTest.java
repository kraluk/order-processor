package io.kraluk.orderprocessor.adapter.orderupdate.web;

import static org.assertj.core.api.Assertions.assertThat;

import io.kraluk.orderprocessor.shared.contract.http.OrderUpdateExecutionHttp;
import io.kraluk.orderprocessor.test.InMemoryTestConfiguration;
import io.kraluk.orderprocessor.test.IntegrationTest;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;

@Import(
    InMemoryTestConfiguration
        .class) // just to not test the whole orchestration machinery in this particular test
class OrderUpdateExecutionsControllerIntegrationTest extends IntegrationTest {

  @Test
  void shouldInitiateOrderUpdateExecution() {
    // Given
    final var source = "test-source";

    // When
    final var result =
        updateExecutionsTestClient().executeProcess(source, OrderUpdateExecutionHttp.class);

    // Then
    assertThat(result)
        .isNotNull()
        .matches(d -> d.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(202)))
        .extracting(HttpEntity::getBody)
        .matches(b -> Objects.equals(b.source(), source))
        .matches(b -> Objects.equals(b.message(), "Order update process has been accepted."))
        .matches(b -> b.timestamp() != null);
  }
}
