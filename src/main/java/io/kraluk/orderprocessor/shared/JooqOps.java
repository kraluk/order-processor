package io.kraluk.orderprocessor.shared;

import static java.lang.String.format;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;

import io.kraluk.orderprocessor.domain.shared.SessionId;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

public final class JooqOps {

  private JooqOps() {
    throw new UnsupportedOperationException("This class should not be instantiated");
  }

  public static <T> Field<T> column(DataType<T> type, String... name) {
    return field(name(name), type);
  }

  public static String temporaryTableName(SessionId sessionId) {
    return format("temp_table_%s", sessionId.value());
  }

  public static Table<Record> temporaryTable(SessionId sessionId) {
    return table(temporaryTableName(sessionId));
  }
}
