package io.kraluk.orderprocessor.adapter.orderupdate.repository;

import static io.kraluk.orderprocessor.test.TestOps.pathTo;
import static org.assertj.core.api.Assertions.assertThat;

import io.kraluk.orderprocessor.test.aws.AwsIntegrationTest;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class S3OrderUpdateRepositoryIntegrationTest extends AwsIntegrationTest {

  @Autowired
  private DefaultOrderUpdateRepository repository;

  @Test
  void shouldFindAllOrderUpdatesStoredInBucket() {
    // Given updates are uploaded
    final var source = "new_orders_single.csv";
    uploadOrderUpdates(source);

    // When
    final var result = repository.findAllFrom(source);

    // Then
    assertThat(result.toList())
        .hasSize(1)
        .first()
        .matches(
            u -> u.getBusinessId().equals(UUID.fromString("10000000-0000-0000-0000-000000000000")))
        .matches(u -> u.getValue().compareTo(BigDecimal.valueOf(100)) == 0)
        .matches(u -> u.getCurrency().equals("PLN"))
        .matches(u -> u.getNotes().equals("note1"))
        .matches(u -> u.getUpdatedAt().equals(Instant.parse("2024-01-01T00:00:00.001Z")));
  }

  private void uploadOrderUpdates(final String source) {
    final var sourcePath = pathTo(String.format("orderupdate/s3/%s", source));
    s3TestClient.upload(source, sourcePath);
  }
}
