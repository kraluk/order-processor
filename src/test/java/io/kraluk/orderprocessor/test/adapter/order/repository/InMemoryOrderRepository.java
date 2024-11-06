package io.kraluk.orderprocessor.test.adapter.order.repository;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.order.port.OrderRepository;
import io.kraluk.orderprocessor.domain.shared.TemporaryTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public final class InMemoryOrderRepository implements OrderRepository {
  private static final Logger log = LoggerFactory.getLogger(InMemoryOrderRepository.class);
  private static final Random RANDOM = new Random();

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

  @SuppressWarnings("unchecked")
  @Override
  public Stream<Order> upsertFromTempTable(final TemporaryTable temporaryTable) {
    if (temporaryTable instanceof InMemoryTemporaryTable) {
      final var table = (InMemoryTemporaryTable<Order>) temporaryTable;
      log.debug("Upserting '{}' Orders from temporary table '{}'", table.getData().size(), table.getName());

      return table.getData()
          .stream()
          .map(InMemoryOrderRepository::withId)
          .peek(order -> orders.put(order.getId(), order));

    } else {
      throw new IllegalArgumentException("Invalid Temporary Table type!");
    }
  }

  public Order save(final Order order) {
    return orders.put(order.getId(), order);
  }

  public List<Order> elements() {
    return List.copyOf(orders.values());
  }

  private static Order withId(final Order given) {
    return new Order(
        RANDOM.nextLong(1_000_000_000),
        given.getBusinessId(),
        given.getValue(),
        given.getNotes(),
        given.getVersion(),
        given.getCreatedAt(),
        given.getUpdatedAt(),
        given.getReadAt()
    );
  }
}
