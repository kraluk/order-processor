package io.kraluk.orderprocessor.configuration.outbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.transaction-outbox")
public record TransactionOutboxProperties(
    Duration resumerDelay,
    int flushBatchSize,
    Duration attemptFrequency) {
}
