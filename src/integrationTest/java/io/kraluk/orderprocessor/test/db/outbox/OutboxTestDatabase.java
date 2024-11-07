package io.kraluk.orderprocessor.test.db.outbox;

import org.springframework.jdbc.core.JdbcTemplate;

public class OutboxTestDatabase {

  private final JdbcTemplate jdbc;

  public OutboxTestDatabase(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public Long count() {
    return jdbc.queryForObject("SELECT COUNT(1) FROM transaction_outbox", Long.class);
  }
}
