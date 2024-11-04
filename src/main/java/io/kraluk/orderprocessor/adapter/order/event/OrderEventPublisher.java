package io.kraluk.orderprocessor.adapter.order.event;

import io.kraluk.orderprocessor.shared.contract.event.OrderUpdatedEvent;

public interface OrderEventPublisher {
  void publish(OrderUpdatedEvent event);
}
