package io.kraluk.batchprocessor.configuration.outbox;

import com.gruelbox.transactionoutbox.TransactionOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * [Background worker](https://github.com/gruelbox/transaction-outbox#set-up-the-background-worker) for the `transaction-outbox`'s failed
 * jobs
 * <p>
 * Consider [clustering](https://github.com/gruelbox/transaction-outbox#clustering) when the load and application instances increase.
 */
class TransactionOutboxStaleJobResumer {
  private static final Logger log = LoggerFactory.getLogger(TransactionOutboxStaleJobResumer.class);

  private final TransactionOutbox outbox;

  public TransactionOutboxStaleJobResumer(final TransactionOutbox outbox) {
    this.outbox = outbox;
  }

  @Scheduled(fixedDelayString = "${app.transaction-outbox.resumer-delay}")
  void flush() {
    try {
      var result = outbox.flush();
      log.debug("Flushing outbox with result - '{}'", result);
    } catch (Exception e) {
      log.error("Unable to flush Transaction Outbox properly!", e);
    }
  }
}
