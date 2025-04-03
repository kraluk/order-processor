package io.kraluk.orderprocessor.test.db.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TestOrder(
    Long id,
    UUID businessId,
    BigDecimal value,
    String currency,
    String notes,
    Long version,
    Instant createdAt,
    Instant updatedAt,
    Instant readAt) {}
