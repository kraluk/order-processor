package io.kraluk.orderprocessor.application.order.event;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.kraluk.orderprocessor.contract.event.OrderUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SqsOrderEventPublisher implements OrderEventPublisher {

  private final SqsTemplate template;

  SqsOrderEventPublisher(final SqsTemplate template) {
    this.template = template;
  }

  @Override
  public void publish(final OrderUpdatedEvent event) {
    template.send(to -> to
        .queue("order-updated")
        .payload(event));
  }
}

@Configuration
class OrderEventPublisherConfiguration {
  private static final Logger log = LoggerFactory.getLogger(OrderEventPublisherConfiguration.class);

  @ConditionalOnProperty(
      prefix = "spring.cloud.aws.sqs",
      name = "enabled",
      havingValue = "true"
  )
  @Bean
  OrderEventPublisher orderEventPublisher(final SqsTemplate template) {
    log.info("Using SQS implementation of the Order Event Publisher");
    return new SqsOrderEventPublisher(template);
  }
}
