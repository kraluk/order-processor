package io.kraluk.orderprocessor.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kraluk.orderprocessor.adapter.order.event.OrderEventPublisher;
import io.kraluk.orderprocessor.adapter.order.web.OrdersWebTestClient;
import io.kraluk.orderprocessor.adapter.orderupdate.repository.OrderUpdateDownloader;
import io.kraluk.orderprocessor.adapter.orderupdate.web.OrderUpdateExecutionsWebTestClient;
import io.kraluk.orderprocessor.test.adapter.orderupdate.repository.StaticOrderUpdateDownloader;
import io.kraluk.orderprocessor.test.db.order.OrderDatabaseTestConfiguration;
import io.kraluk.orderprocessor.test.db.order.OrderTestDatabase;
import io.kraluk.orderprocessor.test.event.LoggingOrderEventPublisher;
import io.kraluk.orderprocessor.test.extension.ClearDatabaseExtension;
import io.kraluk.orderprocessor.test.web.TestRestClientTestConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
@Import({
    TestRestClientTestConfiguration.class,
    NoAwsTestConfiguration.class,
    OrderDatabaseTestConfiguration.class
})
@ExtendWith(ClearDatabaseExtension.class)
public abstract class IntegrationTest {

  @Autowired
  protected OrderTestDatabase orderTestDatabase;

  @Autowired
  protected ObjectMapper mapper;

  @Autowired
  protected RestClient testRestClient;

  protected OrdersWebTestClient ordersTestClient() {
    return new OrdersWebTestClient(testRestClient);
  }

  protected OrderUpdateExecutionsWebTestClient updateExecutionsTestClient() {
    return new OrderUpdateExecutionsWebTestClient(testRestClient);
  }
}

@TestConfiguration
class NoAwsTestConfiguration {
  private static final Logger log = LoggerFactory.getLogger(NoAwsTestConfiguration.class);

  @ConditionalOnProperty(
      prefix = "spring.cloud.aws.sqs",
      name = "enabled",
      havingValue = "false"
  )
  @Bean
  OrderEventPublisher orderEventPublisher() {
    log.warn("Using logging OrderEventPublisher for tests");
    return new LoggingOrderEventPublisher();
  }

  @ConditionalOnProperty(
      prefix = "spring.cloud.aws.s3",
      name = "enabled",
      havingValue = "false"
  )
  @Bean
  OrderUpdateDownloader orderUpdateDownloader() {
    log.warn("Using static OrderUpdateDownloader for tests");
    return new StaticOrderUpdateDownloader();
  }
}