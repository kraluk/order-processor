package io.kraluk.orderprocessor.domain.order.port;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.shared.SessionId;
import io.kraluk.orderprocessor.domain.shared.TemporaryTable;

import java.util.stream.Stream;

public interface OrderTemporaryRepository {

  TemporaryTable createTemporaryTable(final SessionId sessionId);

  TemporaryTable saveInto(final Stream<Order> updates, final TemporaryTable temporaryTable);
}
