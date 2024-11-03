package io.kraluk.batchprocessor.application.outbox;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="app.transaction-outbox")public record TransactionOutboxProperties(Duration resumerDelay,int flushBatchSize,Duration attemptFrequency){}
