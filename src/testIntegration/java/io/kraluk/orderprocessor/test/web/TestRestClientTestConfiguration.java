package io.kraluk.orderprocessor.test.web;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.restclient.RootUriTemplateHandler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import tools.jackson.databind.ObjectMapper;

@TestConfiguration
public class TestRestClientTestConfiguration {

  @Bean(name = "testRestClient")
  public RestClient testRestClient(final Environment environment, final ObjectMapper mapper) {
    final var uriBuilderFactory = new LocalHostUriBuilderFactory(environment);

    final var httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .followRedirects(HttpClient.Redirect.NEVER)
        .build();

    return RestClient.builder()
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
    return new DefaultUriBuilderFactory(delegate.getRootUri()).uriString(uriTemplate);
  }

  @Override
  public UriBuilder builder() {
    return new DefaultUriBuilderFactory(delegate.getRootUri()).uriString("");
  }
}

class NoOpErrorStatusHandler extends DefaultResponseErrorHandler {

  @Override
  public void handleError(
      final ClientHttpResponse response,
      final HttpStatusCode statusCode,
      final @Nullable URI url,
      final @Nullable HttpMethod method) {
    // noop as we want to make assertions on non-200 responses in tests
  }
}

class LocalHostUriTemplateHandler extends RootUriTemplateHandler {
  private static final String PREFIX = "server.servlet.";

  private final Environment environment;
  private final String scheme;

  LocalHostUriTemplateHandler(final Environment environment) {
    this(environment, "http");
  }

  LocalHostUriTemplateHandler(final Environment environment, final String scheme) {
    super(new DefaultUriBuilderFactory());
    requireNonNull(environment, "'environment' must not be null");
    requireNonNull(scheme, "'scheme' must not be null");
    this.environment = environment;
    this.scheme = scheme;
  }

  @Override
  public String getRootUri() {
    final var port = this.environment.getProperty("local.server.port", "8080");
    final var contextPath = this.environment.getProperty(PREFIX + "context-path", "");
    return this.scheme + "://localhost:" + port + contextPath;
  }
}
