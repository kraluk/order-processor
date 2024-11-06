package io.kraluk.orderprocessor.test.adapter.order.repository;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.order.port.OrderTemporaryRepository;
import io.kraluk.orderprocessor.domain.shared.SessionId;
import io.kraluk.orderprocessor.domain.shared.TemporaryTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.stream.Stream;

public final class InMemoryOrderTemporaryRepository implements OrderTemporaryRepository {
  private static final Logger log = LoggerFactory.getLogger(InMemoryOrderTemporaryRepository.class);

  @Override
  public TemporaryTable createTemporaryTable(final SessionId sessionId) {
    log.debug("Creating a new temporary table for session '{}'", sessionId.value());
    return new InMemoryTemporaryTable<>(sessionId.value(), Collections.<Order>emptyList());
  }

  @SuppressWarnings("unchecked")
  @Override
  public TemporaryTable saveInto(final Stream<Order> updates, final TemporaryTable temporaryTable) {
    if (temporaryTable instanceof InMemoryTemporaryTable) {
      final var table = (InMemoryTemporaryTable<Order>) temporaryTable;

      final var data = updates.toList();
      table.getData().addAll(data);

      log.debug("Saved '{}' elements to temporary table '{}'", data.size(), table.getName());
      return table;
    } else {
      throw new IllegalArgumentException("Illegal type of the TemporaryTable!");
    }
  }
}
