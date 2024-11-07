package io.kraluk.orderprocessor.test.adapter.orderupdate.repository;

import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdate;
import io.kraluk.orderprocessor.domain.orderupdate.port.OrderUpdateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class InMemoryOrderUpdateRepository implements OrderUpdateRepository {
  private static final Logger log = LoggerFactory.getLogger(InMemoryOrderUpdateRepository.class);

  private final Map<String, List<OrderUpdate>> updates = new ConcurrentHashMap<>();

  @Override
  public Stream<OrderUpdate> findAllFrom(final String source) {
    log.debug("Finding all Order Updates from the source '{}'", source);
    return Optional.ofNullable(updates.get(source))
        .stream()
        .flatMap(List::stream);
  }

  public void save(final String source, final List<OrderUpdate> orderUpdates) {
    updates.put(source, orderUpdates);
  }

  public List<OrderUpdate> elementsOf(final String source) {
    return Optional.ofNullable(updates.get(source))
        .orElseGet(Collections::emptyList);
  }
}
