package io.kraluk.orderprocessor.shared.contract.http;

import io.kraluk.orderprocessor.test.domain.order.entity.TestOrderBuilder;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class OrderHttpTest {

  @Test
  void shouldCreateFromDomain() {
    // Given
    final var domain = TestOrderBuilder.buildRandom();

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