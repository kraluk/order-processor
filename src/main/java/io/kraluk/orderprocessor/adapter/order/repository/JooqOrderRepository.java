package io.kraluk.orderprocessor.adapter.order.repository;

import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.ALL_COLUMNS;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.BUSINESS_ID;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.CREATED_AT;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.CURRENCY;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.ID;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.NOTES;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.ORDER_TABLE;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.READ_AT;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.UPDATED_AT;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.VALUE;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.VERSION;
import static io.kraluk.orderprocessor.shared.JooqOps.column;
import static org.jooq.impl.DSL.currentInstant;
import static org.jooq.impl.DSL.excluded;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.val;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.order.port.OrderRepository;
import io.kraluk.orderprocessor.domain.shared.TemporaryTable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.javamoney.moneta.Money;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.SQLDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;

@Repository
public class JooqOrderRepository implements OrderRepository {
  private static final Logger log = LoggerFactory.getLogger(JooqOrderRepository.class);

  private final DSLContext dsl;
  private final OrderBatchProperties properties;

  public JooqOrderRepository(final DSLContext dsl, final OrderBatchProperties properties) {
    this.dsl = dsl;
    this.properties = properties;
  }

  @Override
  public Optional<Order> findById(Long id) {
    return dsl.select(ALL_COLUMNS)
        .from(ORDER_TABLE)
        .where(ID.equal(id))
        .fetchOptional(OrderSchema::toOrder);
  }

  @Override
  public Optional<Order> findByBusinessId(UUID businessId) {
    return dsl.select(ALL_COLUMNS)
        .from(ORDER_TABLE)
        .where(BUSINESS_ID.equal(businessId))
        .fetchOptional(OrderSchema::toOrder);
  }

  @Override
  public Stream<Order> upsertFromTempTable(final TemporaryTable temporaryTable) {
    final var tempTable = temporaryTable.getName();

    return dsl
        .insertInto(ORDER_TABLE)
        .columns(BUSINESS_ID, VALUE, CURRENCY, NOTES, VERSION, CREATED_AT, UPDATED_AT, READ_AT)
        .select(dsl.select(
                column(BUSINESS_ID.getDataType(), tempTable, BUSINESS_ID.getName()),
                column(VALUE.getDataType(), tempTable, VALUE.getName()),
                column(CURRENCY.getDataType(), tempTable, CURRENCY.getName()),
                column(NOTES.getDataType(), tempTable, NOTES.getName()),
                val(1L).as(VERSION),
                currentInstant().as(CREATED_AT), // only for new records
                column(UPDATED_AT.getDataType(), tempTable, UPDATED_AT.getName()),
                column(READ_AT.getDataType(), tempTable, READ_AT.getName()))
            .from(table(tempTable)))
        .onConflict(BUSINESS_ID)
        .doUpdate()
        .setAllToExcluded()
        .set(VERSION, VERSION.plus(1))
        .where(UPDATED_AT.lt(excluded(UPDATED_AT)))
        .returning(ALL_COLUMNS)
        .fetchSize(properties.size())
        .fetchLazy()
        .stream()
        .map(OrderSchema::toOrder);
  }
}

// FEATURE: generate jOOQ's database meta model
interface OrderSchema {

  static Order toOrder(Record record) {
    return new Order(
        record.get(ID),
        record.get(BUSINESS_ID),
        Money.of(record.get(VALUE), record.get(CURRENCY)),
        record.get(NOTES),
        record.get(VERSION),
        record.get(CREATED_AT),
        record.get(UPDATED_AT),
        record.get(READ_AT));
  }

  Table<?> ORDER_TABLE = table("orders");

  Field<Long> ID = column(SQLDataType.BIGINT, ORDER_TABLE.getName(), "id");
  Field<UUID> BUSINESS_ID = column(SQLDataType.UUID, ORDER_TABLE.getName(), "business_id");

  Field<BigDecimal> VALUE = column(SQLDataType.NUMERIC, ORDER_TABLE.getName(), "value");
  Field<String> CURRENCY = column(SQLDataType.VARCHAR(3), ORDER_TABLE.getName(), "currency");
  Field<String> NOTES = column(SQLDataType.CLOB, ORDER_TABLE.getName(), "notes");

  Field<Long> VERSION = column(SQLDataType.BIGINT, ORDER_TABLE.getName(), "version");
  Field<Instant> CREATED_AT = column(SQLDataType.INSTANT, ORDER_TABLE.getName(), "created_at");
  Field<Instant> UPDATED_AT = column(SQLDataType.INSTANT, ORDER_TABLE.getName(), "updated_at");
  Field<Instant> READ_AT = column(SQLDataType.INSTANT, ORDER_TABLE.getName(), "read_at");

  Field<?>[] ALL_COLUMNS =
      new Field[] {ID, BUSINESS_ID, VALUE, CURRENCY, NOTES, VERSION, CREATED_AT, UPDATED_AT, READ_AT
      };
}

@ConfigurationProperties(prefix = "app.order.batch")
record OrderBatchProperties(int size) {}
