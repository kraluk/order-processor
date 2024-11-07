package io.kraluk.orderprocessor.domain.orderupdate.entity;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OrderUpdate {

  private final UUID businessId;
  private final BigDecimal value;
  private final String currency;
  private final String notes;
  private final Instant updatedAt;

  public OrderUpdate(
      final UUID businessId,
      final BigDecimal value,
      final String currency,
      final String notes,
      final Instant updatedAt) {
    this.businessId = requireNonNull(businessId);
    this.value = requireNonNull(value);
    this.currency = requireNonNull(currency);
    this.notes = notes;
    this.updatedAt = requireNonNull(updatedAt);
  }

  public UUID getBusinessId() {
    return businessId;
  }

  public BigDecimal getValue() {
    return value;
  }

  public String getCurrency() {
    return currency;
  }

  public String getNotes() {
    return notes;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
