package io.kraluk.orderprocessor.configuration.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruelbox.transactionoutbox.DefaultPersistor;
import com.gruelbox.transactionoutbox.Dialect;
import com.gruelbox.transactionoutbox.ExecutorSubmitter;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import com.gruelbox.transactionoutbox.jackson.JacksonInvocationSerializer;
import com.gruelbox.transactionoutbox.spring.SpringInstantiator;
import com.gruelbox.transactionoutbox.spring.SpringTransactionManager;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Configuration
@Import({SpringTransactionManager.class, SpringInstantiator.class})
class TransactionOutboxConfiguration {
  private static final Logger log = LoggerFactory.getLogger(TransactionOutboxConfiguration.class);

  @Lazy
  @Bean
  ExecutorService outboxExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor(); // a bit of new stuff, but should work well
  }

  @Bean
  TransactionOutboxStaleJobResumer transactionOutboxStaleJobResumer(
      final TransactionOutbox transactionOutbox) {
    return new TransactionOutboxStaleJobResumer(transactionOutbox);
  }

  @Bean
  TransactionOutbox transactionOutbox(
      final SpringTransactionManager springTransactionManager,
      final SpringInstantiator springInstantiator,
      final ObjectMapper mapper,
      @Qualifier("outboxExecutor") final ExecutorService outboxExecutor,
      final TransactionOutboxProperties properties,
      final MeterRegistry registry) {
    log.info("Creating TransactionOutbox with the following properties - '{}'", properties);

    return TransactionOutbox.builder()
        .instantiator(springInstantiator)
        .transactionManager(springTransactionManager)
        .persistor(DefaultPersistor.builder()
            .dialect(Dialect.POSTGRESQL_9)
            .tableName("transaction_outbox")
            .writeLockTimeoutSeconds(1)
            .migrate(false)
            .serializer(JacksonInvocationSerializer.builder().mapper(mapper).build())
            .build())
        .submitter(ExecutorSubmitter.builder()
            .executor(outboxExecutor)
            .logLevelWorkQueueSaturation(Level.INFO)
            .build())
        .flushBatchSize(properties.flushBatchSize())
        .attemptFrequency(properties.attemptFrequency())
        .listener(new TransactionOutboxMetrics(registry))
        .build();
  }
}

@ConfigurationProperties(prefix = "app.transaction-outbox")
record TransactionOutboxProperties(
    Duration resumerDelay, int flushBatchSize, Duration attemptFrequency) {}
