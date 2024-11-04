package io.kraluk.orderprocessor.shared.contract.http;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static io.kraluk.orderprocessor.domain.order.entity.OrderFixtures.completeOrder;
import static org.assertj.core.api.Assertions.assertThat;

class OrderHttpTest {

  @Test
  void shouldCreateFromDomain() {
    // Given
    final var domain = completeOrder();

    // When
    final var http = OrderHttp.from(domain);

    // Then
    assertThat(http)
        .isNotNull()
        .matches(o -> o.businessId().equals(domain.getBusinessId()))
        .matches(o -> o.value().compareTo(domain.getValue().getNumberStripped()) == 0)
        .matches(o -> o.currency().equals(domain.getValue().getCurrency().getCurrencyCode()))
        .matches(o -> Objects.equals(o.notes(), domain.getNotes()))
        .matches(o -> o.version().equals(domain.getVersion()))
        .matches(o -> o.createdAt().equals(domain.getCreatedAt()))
        .matches(o -> o.updatedAt().equals(domain.getUpdatedAt()));
  }
}