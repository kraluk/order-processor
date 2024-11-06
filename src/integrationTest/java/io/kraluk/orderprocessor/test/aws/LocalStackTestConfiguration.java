package io.kraluk.orderprocessor.test.aws;

import io.kraluk.orderprocessor.test.aws.client.S3TestClient;
import io.kraluk.orderprocessor.test.aws.client.SqsTestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class LocalStackTestConfiguration {

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

  @Bean
  LocalStackOps localStackOps(final LocalStackContainer localstack) {
    return new LocalStackOps(localstack);
  }

  @Bean
  S3TestClient s3TestClient(
      final LocalStackContainer localstack,
      @Value("${app.order.update.s3.bucket-name}") final String bucketName) {
    return new S3TestClient(localstack, bucketName);
  }

  @Bean
  SqsTestClient sqsTestClient(
      final LocalStackContainer localstack,
      @Value("${app.order.event.sqs.queue-name}") final String queueName) {
    return new SqsTestClient(localstack, queueName);
  }
}
