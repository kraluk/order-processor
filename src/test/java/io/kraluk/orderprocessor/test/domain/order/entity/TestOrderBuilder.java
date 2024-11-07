package io.kraluk.orderprocessor.test.domain.order.entity;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import org.javamoney.moneta.Money;

public final class TestOrderBuilder {
  private static final Random RANDOM = new Random();

  private Long id = RANDOM.nextLong(1_000_000_000);
  private UUID businessId = UUID.randomUUID();
  private Money value = Money.of(BigDecimal.valueOf(RANDOM.nextDouble(1_000)), "PLN");
  private String notes = "Some notes";
  private Long version = 1L;
  private Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");
  private Instant updatedAt = Instant.parse("2024-01-01T00:00:00Z");
  private Instant readAt = Instant.parse("2024-11-04T00:00:00Z");

  private TestOrderBuilder() {}

  public TestOrderBuilder id(final Long id) {
    this.id = id;
    return this;
  }

  public TestOrderBuilder businessId(final UUID businessId) {
    this.businessId = businessId;
    return this;
  }

  public TestOrderBuilder value(final Money value) {
    this.value = value;
    return this;
  }

  public TestOrderBuilder notes(final String notes) {
    this.notes = notes;
    return this;
  }

  public TestOrderBuilder version(final Long version) {
    this.version = version;
    return this;
  }

  public TestOrderBuilder createdAt(final Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public TestOrderBuilder updatedAt(final Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public TestOrderBuilder readAt(final Instant readAt) {
    this.readAt = readAt;
    return this;
  }

  public TestOrderBuilder withoutId() {
    this.id = null;
    return this;
  }

  public Order build() {
    return new Order(id, businessId, value, notes, version, createdAt, updatedAt, readAt);
  }

  public static TestOrderBuilder builder() {
    return new TestOrderBuilder();
  }

  public static Order buildRandom() {
    return new TestOrderBuilder().build();
  }
}
