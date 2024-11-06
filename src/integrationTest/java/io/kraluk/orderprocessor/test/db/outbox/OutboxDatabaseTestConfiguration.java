package io.kraluk.orderprocessor.test.db.outbox;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@TestConfiguration
public class OutboxDatabaseTestConfiguration {

  @Bean
  OutboxTestDatabase outboxTestDatabase(final JdbcTemplate jdbc) {
    return new OutboxTestDatabase(jdbc);
  }
}
