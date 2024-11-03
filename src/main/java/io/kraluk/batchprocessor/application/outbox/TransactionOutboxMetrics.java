package io.kraluk.batchprocessor.application.outbox;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;

import com.gruelbox.transactionoutbox.TransactionOutboxEntry;
import com.gruelbox.transactionoutbox.TransactionOutboxListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TransactionOutboxMetrics implements TransactionOutboxListener {
  private static final Logger log = LoggerFactory.getLogger(TransactionOutboxMetrics.class);

  private final Counter scheduled;
  private final Counter success;
  private final Counter failure;
  private final Counter blocked;

  TransactionOutboxMetrics(MeterRegistry registry) {
    this.scheduled = registry.counter("outbox_status", List.of(Tag.of("outcome", "scheduled")));
    this.success = registry.counter("outbox_status", List.of(Tag.of("outcome", "success")));
    this.failure = registry.counter("outbox_status", List.of(Tag.of("outcome", "failure")));
    this.blocked = registry.counter("outbox_status", List.of(Tag.of("outcome", "blocked")));
  }

  @Override
  public void scheduled(TransactionOutboxEntry entry) {
    scheduled.increment();
  }

  @Override
  public void success(TransactionOutboxEntry entry) {
    success.increment();
  }

  @Override
  public void failure(TransactionOutboxEntry entry, Throwable cause) {
    failure.increment();
    log.error("Processing Transaction Outbox entry ended with failure - '{}'", entry.description(), getRootCause(cause));
  }

  @Override
  public void blocked(TransactionOutboxEntry entry, Throwable cause) {
    blocked.increment();
    log.error("Processing Transaction Outbox entry is blocked - '{}'", entry.description(), getRootCause(cause));
  }
}
