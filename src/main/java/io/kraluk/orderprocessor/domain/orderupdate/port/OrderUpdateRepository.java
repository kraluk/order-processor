package io.kraluk.orderprocessor.domain.orderupdate.port;

import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdate;

import java.util.stream.Stream;

public interface OrderUpdateRepository {

  Stream<OrderUpdate> findAllFrom(final String source);
}
