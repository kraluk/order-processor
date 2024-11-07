package io.kraluk.orderprocessor.adapter.orderupdate.repository;

import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdate;
import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdateContent;
import io.kraluk.orderprocessor.shared.StreamOps;
import io.kraluk.orderprocessor.shared.csv.CsvContentSource;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

public interface OrderUpdateParser {
  Stream<OrderUpdate> parse(final OrderUpdateContent content);
}

@Component
class CsvOrderUpdateParser implements OrderUpdateParser {

  @Override
  public Stream<OrderUpdate> parse(final OrderUpdateContent content) {
    final var source = new CsvContentSource<>(IntermediateOrderUpdate.class, content.stream());
    return StreamOps.toStream(source).map(IntermediateOrderUpdate::toDomain);
  }
}

// FEATURE: this record is only an intermediate data model to not use framework-specific annotations
// on the domain OrderUpdate class potentially such optimisation could be not needed, and the domain
// class could be used directly with jackson-annotations dependency
record IntermediateOrderUpdate(
    UUID businessId, BigDecimal value, String currency, String notes, Instant updatedAt) {
  OrderUpdate toDomain() {
    return new OrderUpdate(businessId, value, currency, notes, updatedAt);
  }
}
