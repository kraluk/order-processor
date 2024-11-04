package io.kraluk.orderprocessor.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
class AsyncConfiguration {
  private static final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

  @Bean
  SimpleAsyncTaskExecutor taskExecutor(final SimpleAsyncTaskExecutorBuilder builder) {
    return builder.threadNamePrefix("async-task-").build();
  }
}
