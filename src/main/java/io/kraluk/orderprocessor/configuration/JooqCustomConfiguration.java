package io.kraluk.orderprocessor.configuration;

import org.jooq.conf.RenderNameCase;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JooqCustomConfiguration {

  @Bean
  DefaultConfigurationCustomizer configurationCustomizer() {
    return c -> c.settings().withRenderNameCase(RenderNameCase.LOWER);
  }
}
