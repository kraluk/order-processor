package io.kraluk.orderprocessor.test.aws;

import static java.lang.String.format;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.localstack.LocalStackContainer;

public class LocalStackOps {
  private static final Logger log = LoggerFactory.getLogger(LocalStackOps.class);

  private final LocalStackContainer localstack;

  LocalStackOps(LocalStackContainer localstack) {
    this.localstack = localstack;
  }

  public void createBucket(final String bucketName)
      throws IOException, InterruptedException, IllegalStateException {
    final var result = localstack.execInContainer("awslocal", "s3", "mb", "s3://" + bucketName);
    handleResult(result, "S3 bucket creation", bucketName);
  }

  public void deleteBucket(final String bucketName) throws IOException, InterruptedException {
    final var result =
        localstack.execInContainer("awslocal", "s3", "rb", "s3://" + bucketName, "--force");
    handleResult(result, "S3 bucket removal", bucketName);
  }

  public void createQueue(final String queueName) throws IOException, InterruptedException {
    final var result =
        localstack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", queueName);
    handleResult(result, "SQS queue creation", queueName);
  }

  public void deleteQueue(final String queueName) throws IOException, InterruptedException {
    final var result = localstack.execInContainer(
        "awslocal", "sqs", "delete-queue", "--queue-url", queueUrl(queueName));
    handleResult(result, "SQS queue removal", queueName);
  }

  private String queueUrl(final String queueName) {
    return format("%s/000000000000/%s", localstack.getEndpointOverride(SQS), queueName);
  }

  private void handleResult(
      final Container.ExecResult result, final String process, final String resourceName) {
    if (result.getExitCode() == 0) {
      log.debug(
          "{} for '{}' ended successfully with result - '{}'",
          process,
          resourceName,
          result.getStdout());
    } else {
      throw new IllegalStateException(format(
          "Failed to '%s' for '%s' with result - '%s - %s'",
          process, resourceName, result.getExitCode(), result.getStderr()));
    }
  }
}
