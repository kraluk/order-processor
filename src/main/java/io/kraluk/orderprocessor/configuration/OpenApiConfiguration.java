package io.kraluk.orderprocessor.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfiguration {

  @Bean
  OpenAPI openApi() {
    return new OpenAPI()
        .info(new Info()
            .title("Order Processor API")
            .description("Documentation for the REST API endpoints of the Order Processor")
            .version("v1"));
  }
}
