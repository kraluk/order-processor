package io.kraluk.orderprocessor.configuration.outbox;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;

import com.gruelbox.transactionoutbox.TransactionOutboxEntry;
import com.gruelbox.transactionoutbox.TransactionOutboxListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TransactionOutboxMetrics implements TransactionOutboxListener {
  private static final Logger log = LoggerFactory.getLogger(TransactionOutboxMetrics.class);

  private final Counter scheduledCounter;
  private final Counter successCounter;
  private final Counter failureCounter;
  private final Counter blockedCounter;

  TransactionOutboxMetrics(final MeterRegistry registry) {
    this.scheduledCounter = registry.counter("outbox_status", "outcome", "scheduled");
    this.successCounter = registry.counter("outbox_status", "outcome", "success");
    this.failureCounter = registry.counter("outbox_status", "outcome", "failure");
    this.blockedCounter = registry.counter("outbox_status", "outcome", "blocked");
  }

  @Override
  public void scheduled(final TransactionOutboxEntry entry) {
    scheduledCounter.increment();
  }

  @Override
  public void success(final TransactionOutboxEntry entry) {
    successCounter.increment();
  }

  @Override
  public void failure(final TransactionOutboxEntry entry, final Throwable cause) {
    failureCounter.increment();
    log.error(
        "Processing Transaction Outbox entry ended with failure - '{}'",
        entry.description(),
        getRootCause(cause));
  }

  @Override
  public void blocked(final TransactionOutboxEntry entry, final Throwable cause) {
    blockedCounter.increment();
    log.error(
        "Processing Transaction Outbox entry is blocked - '{}'",
        entry.description(),
        getRootCause(cause));
  }
}
