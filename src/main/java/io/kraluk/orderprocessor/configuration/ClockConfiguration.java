package io.kraluk.orderprocessor.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneOffset;

@Configuration
class ClockConfiguration {
  private static final Logger log = LoggerFactory.getLogger(ClockConfiguration.class);

  private static final ZoneOffset TIME_ZONE = ZoneOffset.UTC;

  @Bean
  Clock clock() {
    log.info("Using Clock with the time zone - '{}'", TIME_ZONE);
    return Clock.system(TIME_ZONE);
  }
}
