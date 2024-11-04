package io.kraluk.orderprocessor.contract.http;

import org.javamoney.moneta.Money;

import java.time.Instant;
import java.util.UUID;

public record OrderHttp(
    UUID businessId,
    Money value,
    String description,
    Instant createdAt,
    Instant updatedAt
) {
}
