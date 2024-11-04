package io.kraluk.orderprocessor.test;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * @see <a href="https://rieckpil.de/amazon-sqs-listener-testing-with-sqstest-spring-cloud-aws/">examples</a>
 * @see <a href="https://docs.awspring.io/spring-cloud-aws/docs/3.2.0/reference/html/index.html#testing">spring cloud aws testing</a>
 */
@Import(LocalstackTestConfiguration.class)
public abstract class AwsIntegrationTest extends IntegrationTest {

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("cloud.aws.sqs.enabled", () -> true);
    registry.add("cloud.aws.s3.enabled", () -> true);
  }
}
