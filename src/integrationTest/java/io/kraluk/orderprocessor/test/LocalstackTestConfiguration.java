package io.kraluk.orderprocessor.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class LocalstackTestConfiguration {

  private static final String LOCAL_STACK_VERSION = "3.8.1";

  @ServiceConnection
  @Bean
  LocalStackContainer localStackContainer() {
    return new LocalStackContainer(
        DockerImageName
            .parse("localstack/localstack")
            .withTag(LOCAL_STACK_VERSION))
        .withServices(
            LocalStackContainer.Service.S3,
            LocalStackContainer.Service.SQS
        );
  }
}
