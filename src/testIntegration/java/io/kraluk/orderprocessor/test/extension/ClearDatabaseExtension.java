package io.kraluk.orderprocessor.test.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class ClearDatabaseExtension implements AfterEachCallback {

  @Override
  public void afterEach(final ExtensionContext context) {
    final var jdbc = SpringExtension.getApplicationContext(context).getBean(JdbcTemplate.class);
    jdbc.execute("TRUNCATE orders CASCADE");
    jdbc.execute("TRUNCATE transaction_outbox CASCADE");
  }
}
