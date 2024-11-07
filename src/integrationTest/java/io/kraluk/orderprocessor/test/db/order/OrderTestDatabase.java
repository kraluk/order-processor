package io.kraluk.orderprocessor.test.db.order;

import io.kraluk.orderprocessor.domain.order.entity.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

  public void save(final Order order) {
    jdbc.update(
        """
            INSERT INTO orders (business_id, value, currency, notes, created_at, updated_at, read_at)
                VALUES (?, ?, ?, ?, ?, ?, ?);
            """,
        order.getBusinessId(),
        order.getValue().getNumber(),
        order.getValue().getCurrency().getCurrencyCode(),
        order.getNotes(),
        order.getCreatedAt() != null ? Timestamp.from(order.getCreatedAt()) : null,
        Timestamp.from(order.getUpdatedAt()),
        order.getReadAt() != null ? Timestamp.from(order.getReadAt()) : null
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
        rs.getTimestamp("created_at").toInstant(),
        rs.getTimestamp("updated_at").toInstant(),
        rs.getTimestamp("read_at").toInstant()
    );
  }
}

