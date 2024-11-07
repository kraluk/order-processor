package io.kraluk.orderprocessor.adapter.order.event;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.kraluk.orderprocessor.shared.contract.event.OrderUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SqsOrderEventPublisher implements OrderEventPublisher {
  private static final Logger log = LoggerFactory.getLogger(SqsOrderEventPublisher.class);

  private final SqsTemplate template;
  private final SqsOrderEventProperties properties;

  SqsOrderEventPublisher(final SqsTemplate template, final SqsOrderEventProperties properties) {
    this.template = template;
    this.properties = properties;
  }

  @Override
  public void publish(final OrderUpdatedEvent event) {
    final var result = template.send(to -> to.queue(properties.queueName()).payload(event));

    log.debug("Published with the result - '{}'", result);
  }
}

@Configuration
class OrderEventPublisherConfiguration {
  private static final Logger log = LoggerFactory.getLogger(OrderEventPublisherConfiguration.class);

  @ConditionalOnProperty(prefix = "spring.cloud.aws.sqs", name = "enabled", havingValue = "true")
  @Bean
  OrderEventPublisher orderEventPublisher(
      final SqsTemplate template, final SqsOrderEventProperties properties) {
    log.info(
        "Using SQS implementation of the Order Event Publisher with the following properties - '{}'",
        properties);
    return new SqsOrderEventPublisher(template, properties);
  }
}

@ConfigurationProperties(prefix = "app.order.event.sqs")
record SqsOrderEventProperties(String queueName) {}
