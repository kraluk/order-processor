package io.kraluk.orderprocessor.contract.event;

import org.javamoney.moneta.Money;

import java.time.Instant;
import java.util.UUID;

public record OrderUpdatedEvent(
    UUID businessId,
    Money value,
    String description,
    Instant createdAt,
    Instant updatedAt
) {
}
