package io.kraluk.orderprocessor.test.aws.testcontainers;

import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;

public interface AwsClientFactory {
  <CLIENT, BUILDER extends AwsClientBuilder<BUILDER, CLIENT>> CLIENT create(BUILDER builder);
}
