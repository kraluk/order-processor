package io.kraluk.orderprocessor.shared.contract.bucket;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderUpdate(
    UUID businessId,
    BigDecimal value,
    String currency,
    String notes
) {
}
