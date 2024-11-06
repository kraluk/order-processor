package io.kraluk.orderprocessor.test.aws;

import io.kraluk.orderprocessor.test.IntegrationTest;
import io.kraluk.orderprocessor.test.aws.client.S3TestClient;
import io.kraluk.orderprocessor.test.aws.client.SqsTestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.UUID;

import static java.lang.String.format;

/**
 * @see <a href="https://rieckpil.de/amazon-sqs-listener-testing-with-sqstest-spring-cloud-aws/">examples</a>
 * @see <a href="https://docs.awspring.io/spring-cloud-aws/docs/3.2.0/reference/html/index.html#testing">spring cloud aws testing</a>
 */
@Import({
    LocalStackTestConfiguration.class
})
public abstract class AwsIntegrationTest extends IntegrationTest {
  private static final String BUCKET_NAME = format("order-updates-bucket-%s", UUID.randomUUID());
  private static final String QUEUE_NAME = format("order-updated-queue-%s", UUID.randomUUID());

  @Autowired
  protected LocalStackOps localStack;

  @Autowired
  protected S3TestClient s3TestClient;

  @Autowired
  protected SqsTestClient sqsTestClient;

  @Value("${app.order.update.s3.bucket-name}")
  protected String bucketName;

  @Value("${app.order.event.sqs.queue-name}")
  protected String queueName;

  @BeforeEach
  void setUp() throws IOException, InterruptedException {
    localStack.createBucket(bucketName);
    localStack.createQueue(queueName);
  }

  @AfterEach
  void cleanUp() throws IOException, InterruptedException {
    localStack.deleteBucket(bucketName);
    localStack.deleteQueue(queueName);
  }

  @DynamicPropertySource
  protected static void properties(final DynamicPropertyRegistry registry) {
    registry.add("spring.cloud.aws.sqs.enabled", () -> true);
    registry.add("spring.cloud.aws.s3.enabled", () -> true);

    registry.add("app.order.update.s3.bucket-name", () -> BUCKET_NAME);
    registry.add("app.order.event.sqs.queue-name", () -> QUEUE_NAME);
  }
}