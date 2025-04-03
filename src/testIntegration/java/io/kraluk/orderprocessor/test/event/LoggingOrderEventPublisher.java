package io.kraluk.orderprocessor.test.event;

import io.kraluk.orderprocessor.adapter.order.event.OrderEventPublisher;
import io.kraluk.orderprocessor.shared.contract.event.OrderUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingOrderEventPublisher implements OrderEventPublisher {
  private static final Logger log = LoggerFactory.getLogger(LoggingOrderEventPublisher.class);

  @Override
  public void publish(final OrderUpdatedEvent event) {
    log.info("Publishing event - '{}'", event);
  }
}
