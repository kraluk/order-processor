package io.kraluk.orderprocessor.test.aws.client;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3TestClient {
  private static final Logger log = LoggerFactory.getLogger(S3TestClient.class);

  private final S3Client client;
  private final String bucketName;

  public S3TestClient(final LocalStackContainer localstack, final String bucketName) {
    this.bucketName = bucketName;
    this.client = S3Client.builder()
        .endpointOverride(localstack.getEndpoint())
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
        .region(Region.of(localstack.getRegion()))
        .build();
  }

  public void upload(final String name, final Path data) {
    log.debug(
        "Uploading file '{}' to bucket '{}' from '{}'", name, bucketName, data.toAbsolutePath());

    final var request = PutObjectRequest.builder().bucket(bucketName).key(name).build();

    final var response = client.putObject(request, data);
    log.debug("Uploaded file '{}' to bucket '{}' with result '{}'", name, bucketName, response);
  }
}
