package io.kraluk.orderprocessor.test;

import io.kraluk.orderprocessor.test.aws.AwsIntegrationTest;
import io.kraluk.orderprocessor.test.db.outbox.OutboxDatabaseTestConfiguration;
import io.kraluk.orderprocessor.test.db.outbox.OutboxTestDatabase;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(OutboxDatabaseTestConfiguration.class)
public abstract class AcceptanceTest extends AwsIntegrationTest {

  @Autowired
  protected OutboxTestDatabase outboxTestDatabase;

  @Autowired
  protected MeterRegistry meterRegistry;
}
