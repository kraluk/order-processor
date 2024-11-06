package io.kraluk.orderprocessor.test.db.order;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class OrderTestDatabase {

  private final JdbcTemplate jdbc;

  public OrderTestDatabase(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public TestOrder findById(final Long id) {
    return jdbc.queryForObject(
        "SELECT * FROM orders WHERE id = ?",
        new TestOrderRowMapper(),
        id
    );
  }

  public TestOrder findByBusinessId(final UUID businessId) {
    return jdbc.queryForObject(
        "SELECT * FROM orders WHERE business_id = ?",
        new TestOrderRowMapper(),
        businessId
    );
  }

  public Long count() {
    return jdbc.queryForObject(
        "SELECT COUNT(1) FROM orders",
        Long.class
    );
  }
}

class TestOrderRowMapper implements RowMapper<TestOrder> {

  @Override
  public TestOrder mapRow(final ResultSet rs, final int rowNum) throws SQLException {
    return new TestOrder(
        rs.getLong("id"),
        UUID.fromString(rs.getString("business_id")),
        rs.getBigDecimal("value"),
        rs.getString("currency"),
        rs.getString("notes"),
        rs.getLong("version"),
        Instant.ofEpochMilli(rs.getLong("created_at")),
        Instant.ofEpochMilli(rs.getLong("updated_at")),
        Instant.ofEpochMilli(rs.getLong("read_at"))
    );
  }
}

