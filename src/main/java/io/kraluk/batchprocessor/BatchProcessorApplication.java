package io.kraluk.batchprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BatchProcessorApplication {

  public static void main(final String[] args) {
    SpringApplication.run(BatchProcessorApplication.class, args);
  }
}
