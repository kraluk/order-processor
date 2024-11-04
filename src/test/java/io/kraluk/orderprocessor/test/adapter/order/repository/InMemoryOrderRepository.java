package io.kraluk.orderprocessor.test.adapter.order.repository;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.order.port.OrderRepository;

import java.util.*;

public final class InMemoryOrderRepository implements OrderRepository {
  private final Map<Long, Order> orders = new HashMap<>();

  @Override
  public Optional<Order> findById(final Long id) {
    return Optional.ofNullable(orders.get(id));
  }

  @Override
  public Optional<Order> findByBusinessId(final UUID businessId) {
    return orders.values().stream()
        .filter(order -> order.getBusinessId().equals(businessId))
        .findFirst();
  }

  public Order save(final Order order) {
    return orders.put(order.getId(), order);
  }

  public List<Order> elements() {
    return List.copyOf(orders.values());
  }
}
