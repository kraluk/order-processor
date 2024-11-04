package io.kraluk.batchprocessor.application.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruelbox.transactionoutbox.DefaultPersistor;
import com.gruelbox.transactionoutbox.Dialect;
import com.gruelbox.transactionoutbox.ExecutorSubmitter;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import com.gruelbox.transactionoutbox.jackson.JacksonInvocationSerializer;
import com.gruelbox.transactionoutbox.spring.SpringInstantiator;
import com.gruelbox.transactionoutbox.spring.SpringTransactionManager;
import com.gruelbox.transactionoutbox.spring.SpringTransactionOutboxConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Import(SpringTransactionOutboxConfiguration.class)
class TransactionOutboxConfiguration {
  private static final Logger log = LoggerFactory.getLogger(TransactionOutboxConfiguration.class);

  @Bean
  SpringInstantiator springInstantiator(final ApplicationContext applicationContext) {
    return new SpringInstantiator(applicationContext);
  }

  @Lazy
  @Bean
  ExecutorService outboxExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor(); // bit new stuff, but should work well
  }

  @Bean
  TransactionOutboxStaleJobResumer transactionOutboxStaleJobResumer(final TransactionOutbox transactionOutbox) {
    return new TransactionOutboxStaleJobResumer(transactionOutbox);
  }

  @Bean
  TransactionOutbox transactionOutbox(final SpringTransactionManager springTransactionManager,
                                      final SpringInstantiator springInstantiator,
                                      final ObjectMapper mapper,
                                      final @Qualifier("outboxExecutor") ExecutorService outboxExecutor,
                                      final TransactionOutboxProperties properties,
                                      final MeterRegistry registry) {
    log.info("Creating TransactionOutbox with the following properties - '{}'", properties);

    return TransactionOutbox.builder()
        .instantiator(springInstantiator)
        .transactionManager(springTransactionManager)
        .persistor(
            DefaultPersistor.builder()
                .dialect(Dialect.POSTGRESQL_9)
                .tableName("transaction_outbox")
                .writeLockTimeoutSeconds(1)
                .migrate(false)
                .serializer(
                    JacksonInvocationSerializer.builder()
                        .mapper(mapper)
                        .build()
                )
                .build()
        )
        .submitter(
            ExecutorSubmitter.builder()
                .executor(outboxExecutor)
                .logLevelWorkQueueSaturation(Level.INFO)
                .build()
        )
        .flushBatchSize(properties.flushBatchSize())
        .attemptFrequency(properties.attemptFrequency())
        .listener(new TransactionOutboxMetrics(registry))
        .build();
  }
}
