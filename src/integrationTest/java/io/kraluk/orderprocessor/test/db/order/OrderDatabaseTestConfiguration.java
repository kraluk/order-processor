package io.kraluk.orderprocessor.test.db.order;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@TestConfiguration
public class OrderDatabaseTestConfiguration {

  @Bean
  OrderTestDatabase testOrderDatabase(final JdbcTemplate jdbc) {
    return new OrderTestDatabase(jdbc);
  }
}
