package io.kraluk.orderprocessor.test.aws.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.net.URI;
import java.util.List;

import static java.lang.String.format;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

public class SqsTestClient {
  private static final Logger log = LoggerFactory.getLogger(SqsTestClient.class);

  private final SqsClient client;
  private final URI endpointUrl;
  private final String queueName;

  public SqsTestClient(final LocalStackContainer localstack, final String queueName) {
    this.queueName = queueName;
    this.endpointUrl = localstack.getEndpointOverride(SQS);
    this.client = SqsClient
        .builder()
        .endpointOverride(localstack.getEndpoint())
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
            )
        )
        .region(Region.of(localstack.getRegion()))
        .build();
  }

  public List<Message> poll(final int noOfMessages) {
    log.debug("Polling '{}' messages from queue '{}'", noOfMessages, queueName);

    final var request = ReceiveMessageRequest.builder()
        .maxNumberOfMessages(noOfMessages)
        .queueUrl(queueUrl(queueName))
        .waitTimeSeconds(1)
        .visibilityTimeout(30)
        .build();

    final var response = client.receiveMessage(request);
    final var result = response.messages();
    log.debug("Received '{}' messages from queue '{}' with result '{}'", result.size(), queueName, response);
    return result;
  }

  private String queueUrl(final String queueName) {
    return format("%s/000000000000/%s", endpointUrl, queueName);
  }
}
