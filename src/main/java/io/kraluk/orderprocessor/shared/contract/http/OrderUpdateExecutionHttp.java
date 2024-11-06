package io.kraluk.orderprocessor.shared.contract.http;

import java.time.Instant;

public record OrderUpdateExecutionHttp(
    String source,
    String message,
    Instant timestamp) {
}
