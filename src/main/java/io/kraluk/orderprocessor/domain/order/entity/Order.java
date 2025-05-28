package io.kraluk.orderprocessor.domain.order.entity;

import java.time.Instant;
import java.util.UUID;
import org.javamoney.moneta.Money;
import org.jspecify.annotations.Nullable;

public final class Order {

  @Nullable
  private final Long id;

  private final UUID businessId;

  private Money value;
  private String notes;

  @Nullable // TODO: should not be null
  private final Long version;

  private final Instant createdAt;
  private Instant updatedAt;
  private Instant readAt;

  public Order(
      @Nullable Long id,
      UUID businessId,
      Money value,
      String notes,
      @Nullable Long version,
      Instant createdAt,
      Instant updatedAt,
      Instant readAt) {
    this.id = id;
    this.businessId = businessId;
    this.value = value;
    this.notes = notes;
    this.version = version;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.readAt = readAt;
  }

  public boolean hasIllegalValue() {
    return value.isNegativeOrZero();
  }

  @Nullable
  public Long getId() {
    return id;
  }

  public UUID getBusinessId() {
    return businessId;
  }

  public Money getValue() {
    return value;
  }

  public String getNotes() {
    return notes;
  }

  @Nullable
  public Long getVersion() {
    return version;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Instant getReadAt() {
    return readAt;
  }

  public static Order fromUpdate(
      UUID businessId, Money value, String notes, Instant updatedAt, Instant readAt) {
    // TODO: createdAt should have its own value, temporary workaround
    return new Order(null, businessId, value, notes, null, updatedAt, updatedAt, readAt);
  }
}
