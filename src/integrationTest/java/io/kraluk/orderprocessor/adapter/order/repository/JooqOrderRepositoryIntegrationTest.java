package io.kraluk.orderprocessor.adapter.order.repository;

import io.kraluk.orderprocessor.domain.order.port.OrderRepository;
import io.kraluk.orderprocessor.domain.order.port.OrderTemporaryRepository;
import io.kraluk.orderprocessor.domain.shared.SessionId;
import io.kraluk.orderprocessor.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static io.kraluk.orderprocessor.domain.order.entity.OrderFixtures.orderWithBusinessId;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "classpath:order/db/initial.sql")
class JooqOrderRepositoryIntegrationTest extends IntegrationTest {

  @Autowired
  private OrderRepository repository;

  @Autowired
  private OrderTemporaryRepository temporaryRepository;

  @Transactional
  @Test
  void shouldUpsertOrderUsingTemporaryTable() {
    // Given
    final var updated = orderWithBusinessId(UUID.fromString("16eb25dd-a5ee-4e16-b7de-9fb3d4d94e11"), BigDecimal.valueOf(999.99));
    final var newest = orderWithBusinessId(UUID.fromString("96eb25dd-a5ee-4e16-b7de-9fb3d4d94e11"), BigDecimal.valueOf(111.11));

    // And given order is in temporary table
    final var tempTable = temporaryRepository.createTemporaryTable(SessionId.random());
    temporaryRepository.saveInto(Stream.of(updated, newest), tempTable);

    // When & Then
    try (final var results = repository.upsertFromTempTable(tempTable)) {
      final var complete = assertThat(results.toList())
          .hasSize(2);

      // And then check the updated order
      complete.filteredOn(o -> updated.getBusinessId().equals(o.getBusinessId()))
          .hasSize(1)
          .first()
          .matches(o -> o.getBusinessId().equals(updated.getBusinessId()))
          .matches(o -> o.getValue().equals(updated.getValue()))
          .matches(o -> o.getVersion() == updated.getVersion() + 1);

      // And then check the newly created order
      complete.filteredOn(o -> newest.getBusinessId().equals(o.getBusinessId()))
          .hasSize(1)
          .first()
          .matches(o -> o.getBusinessId().equals(newest.getBusinessId()))
          .matches(o -> o.getValue().equals(newest.getValue()))
          .matches(o -> o.getVersion() == 1);
    }
  }
}
