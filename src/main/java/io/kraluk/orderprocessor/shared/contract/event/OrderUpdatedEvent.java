package io.kraluk.orderprocessor.shared.contract.event;

import org.javamoney.moneta.Money;

import java.time.Instant;
import java.util.UUID;

public record OrderUpdatedEvent(
    UUID businessId,
    Money value,
    String notes,
    Instant createdAt,
    Instant updatedAt
) {
}
