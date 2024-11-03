package io.kraluk.batchprocessor.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class LocalstackTestConfiguration {

  private static final String LOCAL_STACK_VERSION = "3.8.1";

  // @ServiceConnection(name = "s3")
  @Bean
  LocalStackContainer localStackContainer() {
    return new LocalStackContainer(DockerImageName.parse(String.format("localstack/localstack:%s", LOCAL_STACK_VERSION)))
        .withServices(LocalStackContainer.Service.S3);
  }
}
