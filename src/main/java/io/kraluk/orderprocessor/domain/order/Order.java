package io.kraluk.orderprocessor.domain.order;

import org.javamoney.moneta.Money;

import java.time.Instant;
import java.util.UUID;

public final class Order {

  private final Long id;
  private final UUID businessId;
  private Money value;
  private String description;
  private final Instant createdAt;
  private Instant updatedAt;

  public Order(Long id, UUID businessId, Instant createdAt, Money value, String description, Instant updatedAt) {
    this.id = id;
    this.businessId = businessId;
    this.createdAt = createdAt;
    this.value = value;
    this.description = description;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public UUID getBusinessId() {
    return businessId;
  }

  public Money getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
