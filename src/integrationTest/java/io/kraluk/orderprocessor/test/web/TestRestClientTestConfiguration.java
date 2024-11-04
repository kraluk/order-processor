package io.kraluk.orderprocessor.test.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Map;

@TestConfiguration
public class TestRestClientTestConfiguration {

  @Bean(name = "testRestClient")
  public RestClient testRestClient(
      final Environment environment,
      final ObjectMapper mapper) {
    final var uriBuilderFactory = new LocalHostUriBuilderFactory(environment);

    final var httpClient = HttpClient
        .newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .followRedirects(HttpClient.Redirect.NEVER)
        .build();

    return RestClient.builder()
        .messageConverters(converters -> converters
            .stream()
            .filter(MappingJackson2HttpMessageConverter.class::isInstance)
            .forEach(converter -> ((MappingJackson2HttpMessageConverter) converter).setObjectMapper(mapper)))
        .uriBuilderFactory(uriBuilderFactory)
        .defaultStatusHandler(new NoOpErrorStatusHandler())
        .requestFactory(new JdkClientHttpRequestFactory(httpClient))
        .build();
  }
}

class LocalHostUriBuilderFactory extends DefaultUriBuilderFactory {

  private final LocalHostUriTemplateHandler delegate;

  LocalHostUriBuilderFactory(final Environment environment) {
    this.delegate = new LocalHostUriTemplateHandler(environment);
  }

  @Override
  public URI expand(final String uriTemplate, final Map<String, ?> uriVariables) {
    return delegate.expand(uriTemplate, uriVariables);
  }

  @Override
  public URI expand(final String uriTemplate, final Object... uriVariables) {
    return delegate.expand(uriTemplate, uriVariables);
  }

  @Override
  public UriBuilder uriString(final String uriTemplate) {
    return new DefaultUriBuilderFactory(delegate.getRootUri())
        .uriString(uriTemplate);
  }

  @Override
  public UriBuilder builder() {
    return new DefaultUriBuilderFactory(delegate.getRootUri())
        .uriString("");
  }
}

class NoOpErrorStatusHandler extends DefaultResponseErrorHandler {

  @Override
  public void handleError(final ClientHttpResponse response) {
    // noop as we want to make assertions on non-200 responses in tests
  }
}
