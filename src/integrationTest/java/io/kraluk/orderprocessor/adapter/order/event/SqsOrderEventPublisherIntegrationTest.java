package io.kraluk.orderprocessor.adapter.order.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kraluk.orderprocessor.shared.contract.event.OrderUpdatedEvent;
import io.kraluk.orderprocessor.test.aws.AwsIntegrationTest;
import io.kraluk.orderprocessor.test.shared.contract.event.TestOrderUpdatedEventBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class SqsOrderEventPublisherIntegrationTest extends AwsIntegrationTest {

  @Autowired
  private SqsOrderEventPublisher publisher;

  @Test
  void shouldPublishOrderEvent() {
    // Given
    final var event = TestOrderUpdatedEventBuilder.buildRandom();

    // When
    publisher.publish(event);

    // Then
    await()
        .pollInterval(1, SECONDS)
        .atMost(10, SECONDS)
        .ignoreExceptions()
        .untilAsserted(() -> {
          final var messages = sqsTestClient.poll(1);

          assertThat(messages)
              .hasSize(1)
              .first()
              .extracting(m -> deserialize(m.body()))
              .matches(e -> e.businessId().equals(event.businessId()))
              .matches(e -> e.value().equals(event.value()))
              .matches(e -> e.currency().equals(event.currency()))
              .matches(e -> e.createdAt().equals(event.createdAt()))
              .matches(e -> e.updatedAt().equals(event.updatedAt()));
        });
  }

  private OrderUpdatedEvent deserialize(final String json) {
    try {
      return mapper.readValue(json, OrderUpdatedEvent.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
