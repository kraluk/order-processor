package io.kraluk.orderprocessor.shared.contract.event;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderUpdatedEvent(
    UUID businessId,
    BigDecimal value,
    String currency,
    String notes,
    Instant createdAt,
    Instant updatedAt) {
  public static OrderUpdatedEvent from(final Order order) {
    return new OrderUpdatedEvent(
        order.getBusinessId(),
        order.getValue().getNumber().numberValueExact(BigDecimal.class),
        order.getValue().getCurrency().getCurrencyCode(),
        order.getNotes(),
        order.getCreatedAt(),
        order.getUpdatedAt());
  }
}
