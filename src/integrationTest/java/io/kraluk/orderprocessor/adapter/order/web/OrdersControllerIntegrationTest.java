package io.kraluk.orderprocessor.adapter.order.web;

import static org.assertj.core.api.Assertions.assertThat;

import io.kraluk.orderprocessor.shared.contract.http.OrderHttp;
import io.kraluk.orderprocessor.test.IntegrationTest;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "classpath:order/db/initial.sql")
class OrdersControllerIntegrationTest extends IntegrationTest {

  @Test
  void shouldFindById() {
    // Given
    final Long orderId = 1L;

    // When
    final var result = ordersTestClient().getById(orderId, OrderHttp.class);

    // Then
    assertThat(result)
        .isNotNull()
        .matches(d -> d.getStatusCode().is2xxSuccessful())
        .extracting(HttpEntity::getBody)
        .matches(b ->
            Objects.equals(b.businessId(), UUID.fromString("16eb25dd-a5ee-4e16-b7de-9fb3d4d94e11")))
        .matches(b -> b.value().compareTo(BigDecimal.valueOf(100)) == 0);
  }

  @Test
  void shouldFindByBusinessId() {
    // Given
    final UUID businessId = UUID.fromString("16eb25dd-a5ee-4e16-b7de-9fb3d4d94e11");

    // When
    final var result = ordersTestClient().getByBusinessId(businessId, OrderHttp.class);

    // Then
    assertThat(result)
        .isNotNull()
        .matches(d -> d.getStatusCode().is2xxSuccessful())
        .extracting(HttpEntity::getBody)
        .matches(b ->
            Objects.equals(b.businessId(), UUID.fromString("16eb25dd-a5ee-4e16-b7de-9fb3d4d94e11")))
        .matches(b -> b.value().compareTo(BigDecimal.valueOf(100)) == 0);
  }

  @Test
  void shouldNotFindByNotExistingId() {
    // Given
    final Long orderId = Long.MAX_VALUE;

    // When
    final var result = ordersTestClient().getById(orderId, ProblemDetail.class);

    // Then
    assertThat(result)
        .isNotNull()
        .matches(d -> d.getStatusCode() == HttpStatusCode.valueOf(404))
        .extracting(HttpEntity::getBody)
        .matches(b -> Objects.equals(b.getTitle(), "Not Found"))
        .matches(b ->
            Objects.equals(b.getDetail(), "Order with id '9223372036854775807' does not exist!"));
  }
}
