package io.kraluk.orderprocessor.application.order.event;

import io.kraluk.orderprocessor.contract.event.OrderUpdatedEvent;

public interface OrderEventPublisher {
  void publish(OrderUpdatedEvent event);
}
