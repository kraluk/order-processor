package io.kraluk.orderprocessor.configuration.outbox;

import com.gruelbox.transactionoutbox.TransactionOutboxEntry;
import com.gruelbox.transactionoutbox.TransactionOutboxListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;

class TransactionOutboxMetrics implements TransactionOutboxListener {
  private static final Logger log = LoggerFactory.getLogger(TransactionOutboxMetrics.class);

  private final Counter scheduledCounter;
  private final Counter successCounter;
  private final Counter failureCounter;
  private final Counter blockedCounter;

  TransactionOutboxMetrics(MeterRegistry registry) {
    this.scheduledCounter = registry.counter("outbox_status", List.of(Tag.of("outcome", "scheduled")));
    this.successCounter = registry.counter("outbox_status", List.of(Tag.of("outcome", "success")));
    this.failureCounter = registry.counter("outbox_status", List.of(Tag.of("outcome", "failure")));
    this.blockedCounter = registry.counter("outbox_status", List.of(Tag.of("outcome", "blocked")));
  }

  @Override
  public void scheduled(TransactionOutboxEntry entry) {
    scheduledCounter.increment();
  }

  @Override
  public void success(TransactionOutboxEntry entry) {
    successCounter.increment();
  }

  @Override
  public void failure(TransactionOutboxEntry entry, Throwable cause) {
    failureCounter.increment();
    log.error("Processing Transaction Outbox entry ended with failure - '{}'", entry.description(), getRootCause(cause));
  }

  @Override
  public void blocked(TransactionOutboxEntry entry, Throwable cause) {
    blockedCounter.increment();
    log.error("Processing Transaction Outbox entry is blocked - '{}'", entry.description(), getRootCause(cause));
  }
}
