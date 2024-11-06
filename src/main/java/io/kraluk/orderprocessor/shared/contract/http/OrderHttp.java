package io.kraluk.orderprocessor.shared.contract.http;

import io.kraluk.orderprocessor.domain.order.entity.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderHttp(
    UUID businessId,
    BigDecimal value,
    String currency,
    String notes,
    Long version,
    Instant createdAt,
    Instant updatedAt
) {
  public static OrderHttp from(Order order) {
    return new OrderHttp(
        order.getBusinessId(),
        order.getValue().getNumber().numberValueExact(BigDecimal.class),
        order.getValue().getCurrency().getCurrencyCode(),
        order.getNotes(),
        order.getVersion(),
        order.getCreatedAt(),
        order.getUpdatedAt()
    );
  }
}
