package io.kraluk.orderprocessor.configuration;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync(proxyTargetClass = true)
class AsyncConfiguration {

  @Bean
  SimpleAsyncTaskExecutor asyncTaskExecutor(final SimpleAsyncTaskExecutorBuilder builder) {
    return builder.build();
  }

  @Bean
  AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler() {
    return new LoggingAsyncUncaughtExceptionHandler();
  }
}

@Configuration
class AsyncSupportConfiguration implements AsyncConfigurer {

  private final SimpleAsyncTaskExecutor asyncTaskExecutor;
  private final AsyncUncaughtExceptionHandler exceptionHandler;

  AsyncSupportConfiguration(
      final SimpleAsyncTaskExecutor asyncTaskExecutor,
      final AsyncUncaughtExceptionHandler exceptionHandler) {
    this.asyncTaskExecutor = asyncTaskExecutor;
    this.exceptionHandler = exceptionHandler;
  }

  @Override
  public Executor getAsyncExecutor() {
    return asyncTaskExecutor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return exceptionHandler;
  }
}

// FEATURE: better default exception handling for async methods
class LoggingAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
  private static final Logger log =
      LoggerFactory.getLogger(LoggingAsyncUncaughtExceptionHandler.class);

  @Override
  public void handleUncaughtException(
      final Throwable throwable, final Method method, final Object... params) {
    log.error(
        "Uncaught exception in async method '{}' with parameters '{}'", method, params, throwable);
  }
}
