package io.kraluk.orderprocessor.test.adapter.order.outbox;

import io.kraluk.orderprocessor.adapter.order.outbox.OrderTransactionOutbox;
import io.kraluk.orderprocessor.domain.order.entity.Order;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InMemoryOrderTransactionOutbox implements OrderTransactionOutbox {
  private static final Logger log = LoggerFactory.getLogger(InMemoryOrderTransactionOutbox.class);

  private final List<Order> orders = new CopyOnWriteArrayList<>();

  @Override
  public void add(final Order order) {
    log.debug("Adding Order with business id '{}' to the outbox", order.getBusinessId());
    orders.add(order);
  }

  public List<Order> elements() {
    return List.copyOf(orders);
  }
}
