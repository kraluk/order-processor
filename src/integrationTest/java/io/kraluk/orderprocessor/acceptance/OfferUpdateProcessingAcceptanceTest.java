package io.kraluk.orderprocessor.acceptance;

import io.kraluk.orderprocessor.shared.contract.event.OrderUpdatedEvent;
import io.kraluk.orderprocessor.test.AcceptanceTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static io.kraluk.orderprocessor.test.TestOps.pathTo;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Sql(scripts = "classpath:order/db/initial.sql")
class OfferUpdateProcessingAcceptanceTest extends AcceptanceTest {

  @Test
  void shouldSaveCompletelyNewOrdersAndPublishThemToQueue() {
    // Given updates are uploaded
    final var source = "new_orders.csv";
    uploadUpdates(source);

    // When the update is executed
    final var executionResult = updateExecutionTestClient().executeProcess(source, String.class);

    // Then result is successful
    assertThat(executionResult)
        .matches(r -> r.getStatusCode().is2xxSuccessful());

    // And then orders are saved in the database
    await()
        .pollInterval(1, SECONDS)
        .atMost(10, SECONDS)
        .ignoreExceptions()
        .untilAsserted(() -> {
          assertThat(orderTestDatabase.count())
              .isEqualTo(5 + 5); // 5 existing orders + 5 new orders
        });

    // And then outbox is empty
    await()
        .pollInterval(1, SECONDS)
        .atMost(10, SECONDS)
        .ignoreExceptions()
        .untilAsserted(() -> {
          assertThat(outboxTestDatabase.count())
              .isZero();
        });

    // And then order events are published
    await()
        .pollInterval(1, SECONDS)
        .atMost(10, SECONDS)
        .ignoreExceptions()
        .untilAsserted(() -> {
          final var messages = sqsTestClient.poll(5);

          assertThat(messages)
              .hasSize(5)
              .extracting(m -> deserializeEvent(m.body()))
              .extracting(OrderUpdatedEvent::businessId)
              .contains(EXPECTED_NEW_BUSINESS_IDS);
        });
  }

  @Test
  void shouldUpdateExistingOrdersAndPublishThemToQueue() {
    // Given updates are uploaded
    final var source = "existing_orders.csv";
    uploadUpdates(source);

    // When the update is executed
    final var executionResult = updateExecutionTestClient().executeProcess(source, String.class);

    // Then result is successful
    assertThat(executionResult)
        .matches(r -> r.getStatusCode().is2xxSuccessful());

    // And then orders are saved in the database
    await()
        .pollInterval(1, SECONDS)
        .atMost(10, SECONDS)
        .ignoreExceptions()
        .untilAsserted(() -> {
          assertThat(orderTestDatabase.count())
              .isEqualTo(5);
        });

    // And then outbox is empty
    await()
        .pollInterval(1, SECONDS)
        .atMost(10, SECONDS)
        .ignoreExceptions()
        .untilAsserted(() -> {
          assertThat(outboxTestDatabase.count())
              .isZero();
        });

    // And then order events are published
    await()
        .pollInterval(1, SECONDS)
        .atMost(10, SECONDS)
        .ignoreExceptions()
        .untilAsserted(() -> {
          final var messages = sqsTestClient.poll(5);

          assertThat(messages)
              .hasSize(5)
              .extracting(m -> deserializeEvent(m.body()))
              .extracting(OrderUpdatedEvent::businessId)
              .contains(EXPECTED_EXISTING_BUSINESS_IDS);
        });
  }

  private void uploadUpdates(final String source) {
    final var sourcePath = pathTo(String.format("orderupdate/s3/%s", source));
    s3TestClient.upload(source, sourcePath);
  }

  private OrderUpdatedEvent deserializeEvent(final String json) {
    try {
      return mapper.readValue(json, OrderUpdatedEvent.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static final UUID[] EXPECTED_NEW_BUSINESS_IDS = {
      UUID.fromString("10000000-0000-0000-0000-000000000000"),
      UUID.fromString("20000000-0000-0000-0000-000000000000"),
      UUID.fromString("30000000-0000-0000-0000-000000000000"),
      UUID.fromString("40000000-0000-0000-0000-000000000000"),
      UUID.fromString("50000000-0000-0000-0000-000000000000")
  };

  private static final UUID[] EXPECTED_EXISTING_BUSINESS_IDS = {
      UUID.fromString("16eb25dd-a5ee-4e16-b7de-9fb3d4d94e11"),
      UUID.fromString("26eb25dd-a5ee-4e16-b7de-9fb3d4d94e11"),
      UUID.fromString("36eb25dd-a5ee-4e16-b7de-9fb3d4d94e11"),
      UUID.fromString("46eb25dd-a5ee-4e16-b7de-9fb3d4d94e11"),
      UUID.fromString("56eb25dd-a5ee-4e16-b7de-9fb3d4d94e11")
  };
}
