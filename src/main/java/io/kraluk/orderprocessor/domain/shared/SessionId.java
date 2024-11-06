package io.kraluk.orderprocessor.domain.shared;

import java.util.UUID;

public record SessionId(String value) {

  public SessionId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("SessionId cannot be null or blank!");
    }
  }

  public static SessionId random() {
    return new SessionId(UUID.randomUUID().toString().replace("-", "_"));
  }
}
