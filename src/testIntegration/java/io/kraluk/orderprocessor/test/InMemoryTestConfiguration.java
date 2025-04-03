package io.kraluk.orderprocessor.test;

import io.kraluk.orderprocessor.test.adapter.order.repository.InMemoryOrderRepository;
import io.kraluk.orderprocessor.test.adapter.order.repository.InMemoryOrderTemporaryRepository;
import io.kraluk.orderprocessor.test.adapter.orderupdate.repository.InMemoryOrderUpdateRepository;
import io.kraluk.orderprocessor.test.event.LoggingOrderEventPublisher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class InMemoryTestConfiguration {

  @Primary
  @Bean
  InMemoryOrderRepository inMemoryOrderRepository() {
    return new InMemoryOrderRepository();
  }

  @Primary
  @Bean
  InMemoryOrderTemporaryRepository inMemoryOrderTemporaryRepository() {
    return new InMemoryOrderTemporaryRepository();
  }

  @Primary
  @Bean
  InMemoryOrderUpdateRepository inMemoryOrderUpdateRepository() {
    return new InMemoryOrderUpdateRepository();
  }

  @Primary
  @Bean
  LoggingOrderEventPublisher loggingOrderEventPublisher() {
    return new LoggingOrderEventPublisher();
  }
}
