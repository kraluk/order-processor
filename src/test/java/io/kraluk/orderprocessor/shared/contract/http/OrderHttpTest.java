package io.kraluk.orderprocessor.shared.contract.http;

import static org.assertj.core.api.Assertions.assertThat;

import io.kraluk.orderprocessor.test.domain.order.entity.TestOrderBuilder;
import java.util.Objects;
import org.junit.jupiter.api.Test;

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
