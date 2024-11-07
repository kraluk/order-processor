package io.kraluk.orderprocessor.test.shared.contract.event;

import io.kraluk.orderprocessor.shared.contract.event.OrderUpdatedEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static io.kraluk.orderprocessor.test.TestClockOps.ARBITRARY_NOW;

public final class TestOrderUpdatedEventBuilder {
  private UUID businessId = UUID.randomUUID();
  private BigDecimal value = BigDecimal.valueOf(100.99);
  private String currency = "EUR";
  private String notes = null;
  private Instant createdAt = ARBITRARY_NOW;
  private Instant updatedAt = ARBITRARY_NOW;

  private TestOrderUpdatedEventBuilder() {
  }

  public TestOrderUpdatedEventBuilder businessId(UUID businessId) {
    this.businessId = businessId;
    return this;
  }

  public TestOrderUpdatedEventBuilder value(BigDecimal value) {
    this.value = value;
    return this;
  }

  public TestOrderUpdatedEventBuilder currency(String currency) {
    this.currency = currency;
    return this;
  }

  public TestOrderUpdatedEventBuilder notes(String notes) {
    this.notes = notes;
    return this;
  }

  public TestOrderUpdatedEventBuilder createdAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public TestOrderUpdatedEventBuilder updatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public OrderUpdatedEvent build() {
    return new OrderUpdatedEvent(businessId, value, currency, notes, createdAt, updatedAt);
  }

  public static TestOrderUpdatedEventBuilder builder() {
    return new TestOrderUpdatedEventBuilder();
  }

  public static OrderUpdatedEvent buildRandom() {
    return new TestOrderUpdatedEventBuilder().build();
  }
}
