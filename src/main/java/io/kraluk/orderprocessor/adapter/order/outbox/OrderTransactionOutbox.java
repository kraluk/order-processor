package io.kraluk.orderprocessor.adapter.order.outbox;

import com.gruelbox.transactionoutbox.TransactionOutbox;
import io.kraluk.orderprocessor.adapter.order.event.OrderEventPublisher;
import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.shared.contract.event.OrderUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

public interface OrderTransactionOutbox {
  void add(Order order);
}

@Component
class DefaultOrderTransactionOutbox implements OrderTransactionOutbox {
  private static final Logger log = LoggerFactory.getLogger(DefaultOrderTransactionOutbox.class);

  private final TransactionOutbox outbox;
  private final OrderEventPublisher publisher;

  public DefaultOrderTransactionOutbox(final TransactionOutbox outbox, final OrderEventPublisher publisher) {
    this.outbox = outbox;
    this.publisher = publisher;
  }

  @Transactional(propagation = MANDATORY)
  @Override
  public void add(final Order order) {
    final var event = OrderUpdatedEvent.from(order);

    outbox.with()
        .schedule(this.getClass())
        .publish(event);
  }

  public void publish(final OrderUpdatedEvent event) { // has to be public due to outbox proxies
    log.debug("Publishing event related to Order with business id '{}' - '{}'", event.businessId(), event);
    publisher.publish(event);
  }
}

