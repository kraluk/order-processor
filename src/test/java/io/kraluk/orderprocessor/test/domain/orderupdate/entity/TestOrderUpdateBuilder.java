package io.kraluk.orderprocessor.test.domain.orderupdate.entity;

import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public final class TestOrderUpdateBuilder {
  private static final Random RANDOM = new Random();

  private UUID businessId = UUID.randomUUID();
  private BigDecimal value = BigDecimal.valueOf(RANDOM.nextLong(1_000_000));
  private String currency = "PLN";
  private String notes = "some notes";
  private Instant updatedAt = Instant.parse("2024-11-05T18:00:00.000Z");

  private TestOrderUpdateBuilder() {
  }

  public TestOrderUpdateBuilder businessId(UUID businessId) {
    this.businessId = businessId;
    return this;
  }

  public TestOrderUpdateBuilder value(BigDecimal value) {
    this.value = value;
    return this;
  }

  public TestOrderUpdateBuilder currency(String currency) {
    this.currency = currency;
    return this;
  }

  public TestOrderUpdateBuilder notes(String notes) {
    this.notes = notes;
    return this;
  }

  public TestOrderUpdateBuilder updatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public OrderUpdate build() {
    return new OrderUpdate(businessId, value, currency, notes, updatedAt);
  }

  public static TestOrderUpdateBuilder builder() {
    return new TestOrderUpdateBuilder();
  }

  public static OrderUpdate buildRandom() {
    return new TestOrderUpdateBuilder().build();
  }
}
