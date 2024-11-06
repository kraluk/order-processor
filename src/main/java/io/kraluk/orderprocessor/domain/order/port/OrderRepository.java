package io.kraluk.orderprocessor.domain.order.port;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.shared.TemporaryTable;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface OrderRepository {

  Optional<Order> findById(final Long id);

  Optional<Order> findByBusinessId(final UUID businessId);

  Stream<Order> upsertFromTempTable(final TemporaryTable temporaryTable);
}
