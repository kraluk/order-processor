package io.kraluk.orderprocessor.adapter.order.repository;

import io.kraluk.orderprocessor.domain.order.port.OrderRepository;
import io.kraluk.orderprocessor.domain.order.port.OrderTemporaryRepository;
import io.kraluk.orderprocessor.domain.shared.SessionId;
import io.kraluk.orderprocessor.test.IntegrationTest;
import io.kraluk.orderprocessor.test.domain.order.entity.TestOrderBuilder;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "classpath:order/db/initial.sql")
class JooqOrderRepositoryIntegrationTest extends IntegrationTest {

  @Autowired
  private OrderRepository repository;

  @Autowired
  private OrderTemporaryRepository temporaryRepository;

  @Transactional
  @Test
  void shouldUpsertOrdersBasedOnBusinessIdUsingTemporaryTable() {
    // Given
    final var update = TestOrderBuilder.builder()
        .withoutId()
        .businessId(UUID.fromString("16eb25dd-a5ee-4e16-b7de-9fb3d4d94e11")) // based on the `initial.sql` script
        .value(Money.of(BigDecimal.valueOf(999.99), "PLN"))
        .updatedAt(Instant.parse("2024-11-07T23:00:00Z"))
        .build();

    final var create = TestOrderBuilder.builder()
        .withoutId()
        .businessId(UUID.fromString("96eb25dd-a5ee-4e16-b7de-9fb3d4d94e11"))
        .value(Money.of(BigDecimal.valueOf(111.11), "PLN"))
        .updatedAt(Instant.parse("2024-11-07T22:55:00Z"))
        .build();

    // And given order is in temporary table
    final var tempTable = temporaryRepository.createTemporaryTable(SessionId.random());
    temporaryRepository.saveInto(Stream.of(update, create), tempTable);

    // When & Then
    try (final var results = repository.upsertFromTempTable(tempTable)) {
      final var complete = assertThat(results.toList())
          .hasSize(2);

      // And then check the updated order
      complete.filteredOn(o -> update.getBusinessId().equals(o.getBusinessId()))
          .hasSize(1)
          .first()
          .matches(o -> o.getId() != null)
          .matches(o -> o.getBusinessId().equals(update.getBusinessId()))
          .matches(o -> o.getValue().equals(update.getValue()))
          .matches(o -> o.getNotes().equals(update.getNotes()))
          .matches(o -> o.getVersion() == update.getVersion() + 1)
          .matches(o -> o.getCreatedAt() != null)
          .matches(o -> o.getUpdatedAt().equals(update.getUpdatedAt()))
          .matches(o -> o.getReadAt() != null);

      // And then check the newly created order
      complete.filteredOn(o -> create.getBusinessId().equals(o.getBusinessId()))
          .hasSize(1)
          .first()
          .matches(o -> o.getId() != null)
          .matches(o -> o.getBusinessId().equals(create.getBusinessId()))
          .matches(o -> o.getValue().equals(create.getValue()))
          .matches(o -> o.getNotes().equals(create.getNotes()))
          .matches(o -> o.getVersion() == 1)
          .matches(o -> o.getCreatedAt() != null)
          .matches(o -> o.getUpdatedAt().equals(create.getUpdatedAt()))
          .matches(o -> o.getReadAt() != null);
    }
  }

  @Transactional
  @Test
  void shouldUpdateOnlyThoseOffersWithGreaterUpdateAtThanTheOneAlreadyPresentInDatabase() {
    // Given
    final var create = TestOrderBuilder.builder()
        .withoutId()
        .businessId(UUID.fromString("00eb25dd-a5ee-4e16-b7de-9fb3d4d94e00")) // based on the `initial.sql` script
        .updatedAt(Instant.parse("2024-11-07T19:00:00Z"))
        .build();

    // And given order is saved
    orderTestDatabase.save(create);

    // And given Order update is prepared with older updatedAt timestamp
    final var update = TestOrderBuilder.builder()
        .withoutId()
        .businessId(UUID.fromString("00eb25dd-a5ee-4e16-b7de-9fb3d4d94e00")) // based on the `initial.sql` script
        .updatedAt(Instant.parse("2024-10-07T19:00:00Z")) // older timestamp
        .build();

    // And given order updated is in temporary table
    final var tempTable = temporaryRepository.createTemporaryTable(SessionId.random());
    temporaryRepository.saveInto(Stream.of(update), tempTable);

    // When
    try (final var results = repository.upsertFromTempTable(tempTable)) {
      // Then order has not been updated
      assertThat(results.toList()).isEmpty();
    }

    // And then order's updated_at has not changed
    final var found = orderTestDatabase.findByBusinessId(create.getBusinessId());

    assertThat(found)
        .matches(p -> p.updatedAt().equals(create.getUpdatedAt()));
  }
}
