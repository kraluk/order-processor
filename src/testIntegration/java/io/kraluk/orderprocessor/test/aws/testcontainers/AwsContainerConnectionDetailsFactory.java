package io.kraluk.orderprocessor.test.aws.testcontainers;

import io.awspring.cloud.autoconfigure.core.AwsConnectionDetails;
import java.net.URI;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;
import org.testcontainers.localstack.LocalStackContainer;

public class AwsContainerConnectionDetailsFactory
    extends ContainerConnectionDetailsFactory<LocalStackContainer, AwsConnectionDetails> {

  @Override
  protected AwsConnectionDetails getContainerConnectionDetails(
      final ContainerConnectionSource<LocalStackContainer> source) {
    return new AwsContainerConnectionDetails(source);
  }

  private static final class AwsContainerConnectionDetails
      extends ContainerConnectionDetails<LocalStackContainer> implements AwsConnectionDetails {

    private AwsContainerConnectionDetails(
        final ContainerConnectionSource<LocalStackContainer> source) {
      super(source);
    }

    @Override
    public URI getEndpoint() {
      return getContainer().getEndpoint();
    }

    @Override
    public String getRegion() {
      return getContainer().getRegion();
    }

    @Override
    public String getAccessKey() {
      return getContainer().getAccessKey();
    }

    @Override
    public String getSecretKey() {
      return getContainer().getSecretKey();
    }
  }
}
