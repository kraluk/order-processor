package io.kraluk.orderprocessor.contract.bucket;

import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderUpdate(
    UUID businessId,
    BigDecimal value,
    String currency,
    String description
) {
}
