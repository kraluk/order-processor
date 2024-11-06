package io.kraluk.orderprocessor.acceptance;

import io.kraluk.orderprocessor.test.AcceptanceTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static io.kraluk.orderprocessor.test.TestResourceOps.pathTo;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Sql(scripts = "classpath:order/db/initial.sql")
public class OfferUpdateProcessingAcceptanceTest extends AcceptanceTest {

  @Test
  void shouldSaveCompletelyNewOrdersAndPublishThemToQueue() {
    // Given updates are uploaded
    final var source = "new_orders.csv";
    uploadOrderUpdates(source);

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
              .isEqualTo(5 + 5);
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
              .hasSize(5);
        });
  }

  @Test
  void shouldUpdateExistingOrdersAndPublishThemToQueue() {
    // Given updates are uploaded
    final var source = "existing_orders.csv";
    uploadOrderUpdates(source);

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
              .hasSize(5);
        });
  }

  private void uploadOrderUpdates(final String source) {
    final var sourcePath = pathTo(String.format("orderupdate/s3/%s", source));
    s3TestClient.upload(source, sourcePath);
  }
}
