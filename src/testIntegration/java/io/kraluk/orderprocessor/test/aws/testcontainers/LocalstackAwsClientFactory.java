package io.kraluk.orderprocessor.test.aws.testcontainers;

import io.awspring.cloud.autoconfigure.core.AwsClientBuilderConfigurer;
import io.awspring.cloud.autoconfigure.core.AwsProperties;
import io.awspring.cloud.core.region.StaticRegionProvider;
import org.testcontainers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;

public class LocalstackAwsClientFactory implements AwsClientFactory {
  private final AwsClientBuilderConfigurer configurer;

  public LocalstackAwsClientFactory(final LocalStackContainer localstack) {
    this.configurer = clientBuilderConfigurer(localstack);
  }

  @Override
  public <CLIENT, BUILDER extends AwsClientBuilder<BUILDER, CLIENT>> CLIENT create(
      final BUILDER builder) {
    return configurer.configure(builder).build();
  }

  private AwsClientBuilderConfigurer clientBuilderConfigurer(final LocalStackContainer localstack) {
    final var properties = new AwsProperties();
    properties.setEndpoint(localstack.getEndpoint());

    final var credentialsProvider = StaticCredentialsProvider.create(
        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey()));
    final var regionProvider = new StaticRegionProvider(localstack.getRegion());

    return new AwsClientBuilderConfigurer(credentialsProvider, regionProvider, properties);
  }
}
