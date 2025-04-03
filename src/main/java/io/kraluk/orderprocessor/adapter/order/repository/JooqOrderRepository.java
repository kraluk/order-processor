package io.kraluk.orderprocessor.adapter.order.repository;

import static io.kraluk.orderprocessor.jooq.tables.Orders.ORDERS;
import static io.kraluk.orderprocessor.shared.JooqOps.column;
import static org.jooq.impl.DSL.excluded;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.val;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.order.port.OrderRepository;
import io.kraluk.orderprocessor.domain.shared.TemporaryTable;
import io.kraluk.orderprocessor.jooq.tables.records.OrdersRecord;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.javamoney.moneta.Money;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;

@Repository
class JooqOrderRepository implements OrderRepository {

  private final DSLContext dsl;
  private final OrderBatchProperties properties;

  private final OrdersRecordMapper mapper = new OrdersRecordMapper();

  JooqOrderRepository(final DSLContext dsl, final OrderBatchProperties properties) {
    this.dsl = dsl;
    this.properties = properties;
  }

  @Override
  public Optional<Order> findById(Long id) {
    return dsl.selectFrom(ORDERS).where(ORDERS.ID.eq(id)).fetchOptional(mapper);
  }

  @Override
  public Optional<Order> findByBusinessId(UUID businessId) {
    return dsl.selectFrom(ORDERS).where(ORDERS.BUSINESS_ID.equal(businessId)).fetchOptional(mapper);
  }

  @SuppressWarnings("resource") // should be closed in a lower layer
  @Override
  public Stream<Order> upsertFromTempTable(final TemporaryTable temporaryTable) {
    final var tempTable = temporaryTable.getName();

    return dsl
        .insertInto(
            ORDERS,
            ORDERS.BUSINESS_ID,
            ORDERS.VALUE,
            ORDERS.CURRENCY,
            ORDERS.NOTES,
            ORDERS.VERSION,
            ORDERS.UPDATED_AT,
            ORDERS.READ_AT)
        .select(dsl.select(
                column(ORDERS.BUSINESS_ID.getDataType(), tempTable, ORDERS.BUSINESS_ID.getName()),
                column(ORDERS.VALUE.getDataType(), tempTable, ORDERS.VALUE.getName()),
                column(ORDERS.CURRENCY.getDataType(), tempTable, ORDERS.CURRENCY.getName()),
                column(ORDERS.NOTES.getDataType(), tempTable, ORDERS.NOTES.getName()),
                val(1L).as(ORDERS.VERSION),
                column(ORDERS.UPDATED_AT.getDataType(), tempTable, ORDERS.UPDATED_AT.getName()),
                column(ORDERS.READ_AT.getDataType(), tempTable, ORDERS.READ_AT.getName()))
            .from(table(tempTable)))
        .onConflict(ORDERS.BUSINESS_ID)
        .doUpdate()
        .setAllToExcluded()
        .set(ORDERS.VERSION, ORDERS.VERSION.plus(1))
        .where(ORDERS.UPDATED_AT.lt(excluded(ORDERS.UPDATED_AT)))
        .returning()
        .fetchSize(properties.size())
        .fetchLazy()
        .stream()
        .map(mapper::map);
  }
}

final class OrdersRecordMapper implements RecordMapper<OrdersRecord, Order> {

  @Override
  public Order map(OrdersRecord record) {
    return new Order(
        record.getId(),
        record.getBusinessId(),
        Money.of(record.getValue(), record.getCurrency()),
        record.getNotes(),
        record.getVersion(),
        record.getCreatedAt(),
        record.getUpdatedAt(),
        record.getReadAt());
  }
}

@ConfigurationProperties(prefix = "app.order.batch")
record OrderBatchProperties(int size) {}
