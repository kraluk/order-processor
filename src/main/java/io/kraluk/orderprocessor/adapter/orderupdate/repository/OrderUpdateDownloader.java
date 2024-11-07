package io.kraluk.orderprocessor.adapter.orderupdate.repository;

import io.awspring.cloud.s3.S3Template;
import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdateContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public interface OrderUpdateDownloader {
  Optional<OrderUpdateContent> download(final String source);
}

@ConditionalOnProperty(
    prefix = "spring.cloud.aws.s3",
    name = "enabled",
    havingValue = "true"
)
@Component
class S3OrderUpdateDownloader implements OrderUpdateDownloader {
  private static final Logger log = LoggerFactory.getLogger(S3OrderUpdateDownloader.class);

  private final S3Template template;
  private final S3OrderUpdateProperties properties;
  private final Path tempDirectory;

  S3OrderUpdateDownloader(final S3Template template, final S3OrderUpdateProperties properties, final Path tempDirectory) {
    this.template = template;
    this.properties = properties;
    this.tempDirectory = tempDirectory;
  }

  @Autowired
  S3OrderUpdateDownloader(final S3Template template, S3OrderUpdateProperties properties) {
    this(template, properties, tempDirectory());
  }

  @Override
  public Optional<OrderUpdateContent> download(final String source) {
    try {
      final var exists = template.objectExists(properties.bucketName(), source);

      if (exists) {
        return Optional.of(downloadFile(source));
      } else {
        log.warn("File '{}' does not exist in the S3 bucket '{}'", source, properties.bucketName());
        return Optional.empty();
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private OrderUpdateContent downloadFile(final String fileName) throws IOException {
    final var resource = template.download(properties.bucketName(), fileName);
    log.debug("Processing file '{}' from S3 - '{}'", fileName, resource);

    try (final var remoteContent = resource.getInputStream()) {

      // File names use slash chars for simulating directories - however, it is not supported on local filesystem
      final var localContent = tempDirectory.resolve(fileName.replace("/", "_"));

      log.info("Downloading remote file '{}' to local '{}'", fileName, localContent);
      Files.copy(remoteContent, localContent);
      log.debug("File '{}' downloaded successfully to '{}'", fileName, localContent);

      // FEATURE: potentially support of gzipped files via GZIPInputStream
      final var downloadedStream = new BufferedInputStream(Files.newInputStream(localContent, StandardOpenOption.DELETE_ON_CLOSE));

      return OrderUpdateContent.of(fileName, downloadedStream);
    }
  }

  private static Path tempDirectory() {
    try {
      return Files.createTempDirectory("order-update");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}

@ConfigurationProperties(prefix = "app.order.update.s3")
record S3OrderUpdateProperties(String bucketName) {
}
