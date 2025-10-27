package io.kraluk.orderprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
class OrderProcessorApplication {

  static void main(final String[] args) {
    SpringApplication.run(OrderProcessorApplication.class, args);
  }
}
