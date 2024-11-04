package io.kraluk.orderprocessor.domain.order.port;

import io.kraluk.orderprocessor.domain.order.entity.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

  Optional<Order> findById(Long id);

  Optional<Order> findByBusinessId(UUID businessId);
}
