package io.kraluk.orderprocessor.adapter.order.repository;

import static io.kraluk.orderprocessor.jooq.tables.Orders.ORDERS;
import static io.kraluk.orderprocessor.shared.JooqOps.temporaryTable;
import static org.jooq.impl.DSL.table;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.order.port.OrderTemporaryRepository;
import io.kraluk.orderprocessor.domain.shared.SessionId;
import io.kraluk.orderprocessor.domain.shared.TemporaryTable;
import io.kraluk.orderprocessor.domain.shared.TemporaryTable.DefaultTemporaryTable;
import io.kraluk.orderprocessor.shared.StreamOps;
import java.math.BigDecimal;
import java.util.stream.Stream;
import org.jooq.DSLContext;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;

@Repository
class JooqOrderTemporaryRepository implements OrderTemporaryRepository {

  private final DSLContext dsl;
  private final OrderTemporaryBatchProperties properties;

  JooqOrderTemporaryRepository(
      final DSLContext dsl, final OrderTemporaryBatchProperties properties) {
    this.dsl = dsl;
    this.properties = properties;
  }

  @Override
  public TemporaryTable createTemporaryTable(final SessionId sessionId) {
    final var temporaryTable = temporaryTable(sessionId);

    dsl.createTemporaryTable(temporaryTable)
        .column(ORDERS.BUSINESS_ID)
        .column(ORDERS.VALUE)
        .column(ORDERS.CURRENCY)
        .column(ORDERS.NOTES)
        .column(ORDERS.UPDATED_AT)
        .column(ORDERS.READ_AT)
        .primaryKey(ORDERS.BUSINESS_ID)
        .onCommitDrop()
        .execute();

    return DefaultTemporaryTable.of(temporaryTable.getName());
  }

  @Override
  public TemporaryTable saveInto(final Stream<Order> updates, final TemporaryTable temporaryTable) {
    StreamOps.fixedWindow(updates, properties.size()).forEach(batch -> dsl.batch(batch.stream()
            .map(order -> dsl.insertInto(table(temporaryTable.getName()))
                .set(ORDERS.BUSINESS_ID, order.getBusinessId())
                .set(ORDERS.VALUE, order.getValue().getNumber().numberValue(BigDecimal.class))
                .set(ORDERS.CURRENCY, order.getValue().getCurrency().getCurrencyCode())
                .set(ORDERS.NOTES, order.getNotes())
                .set(ORDERS.UPDATED_AT, order.getUpdatedAt())
                .set(ORDERS.READ_AT, order.getReadAt()))
            .toList())
        .execute());
    return temporaryTable;
  }
}

@ConfigurationProperties(prefix = "app.order.temporary-batch")
record OrderTemporaryBatchProperties(int size) {}
