package io.kraluk.orderprocessor.adapter.order.repository;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import io.kraluk.orderprocessor.domain.order.port.OrderTemporaryRepository;
import io.kraluk.orderprocessor.domain.shared.SessionId;
import io.kraluk.orderprocessor.domain.shared.TemporaryTable;
import io.kraluk.orderprocessor.domain.shared.TemporaryTable.DefaultTemporaryTable;
import io.kraluk.orderprocessor.shared.StreamOps;
import org.jooq.DSLContext;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.BUSINESS_ID;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.CREATED_AT;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.CURRENCY;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.NOTES;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.READ_AT;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.UPDATED_AT;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.VALUE;
import static io.kraluk.orderprocessor.adapter.order.repository.OrderSchema.VERSION;
import static io.kraluk.orderprocessor.shared.JooqOps.temporaryTable;
import static org.jooq.impl.DSL.table;

@Repository
class JooqOrderTemporaryRepository implements OrderTemporaryRepository {

  private final DSLContext dsl;
  private final OrderTemporaryBatchProperties properties;

  JooqOrderTemporaryRepository(final DSLContext dsl, final OrderTemporaryBatchProperties properties) {
    this.dsl = dsl;
    this.properties = properties;
  }

  @Override
  public TemporaryTable createTemporaryTable(final SessionId sessionId) {
    final var temporaryTable = temporaryTable(sessionId);

    dsl.createTemporaryTable(temporaryTable)
        .column(BUSINESS_ID)
        .column(VALUE)
        .column(CURRENCY)
        .column(NOTES)
        .column(VERSION)
        .column(CREATED_AT)
        .column(UPDATED_AT)
        .column(READ_AT)
        .primaryKey(BUSINESS_ID)
        .onCommitDrop()
        .execute();

    return DefaultTemporaryTable.of(temporaryTable.getName());

  }

  @Override
  public TemporaryTable saveInto(final Stream<Order> updates, final TemporaryTable temporaryTable) {
    StreamOps.fixedWindow(updates, properties.size())
        .forEach(batch ->
            dsl.batch(
                batch.stream()
                    .map(order ->
                        dsl.insertInto(table(temporaryTable.getName()))
                            .set(BUSINESS_ID, order.getBusinessId())
                            .set(VALUE, order.getValue().getNumber().numberValue(BigDecimal.class))
                            .set(CURRENCY, order.getValue().getCurrency().getCurrencyCode())
                            .set(NOTES, order.getNotes())
                            .set(VERSION, order.getVersion())
                            .set(CREATED_AT, order.getCreatedAt())
                            .set(UPDATED_AT, order.getUpdatedAt())
                            .set(READ_AT, order.getReadAt())
                    ).toList()
            ).execute());
    return null;
  }
}

@ConfigurationProperties(prefix = "app.order.temporary-batch")
record OrderTemporaryBatchProperties(int size) {
}