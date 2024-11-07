package io.kraluk.orderprocessor.adapter.order.web;

import static io.kraluk.orderprocessor.adapter.order.web.ProblemDetailOps.orderNotFound;
import static java.lang.String.format;

import io.kraluk.orderprocessor.shared.contract.http.OrderHttp;
import io.kraluk.orderprocessor.usecase.order.FindOrderByBusinessIdUseCase;
import io.kraluk.orderprocessor.usecase.order.FindOrderByIdUseCase;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class OrdersController {

  private final FindOrderByIdUseCase byIdUseCase;
  private final FindOrderByBusinessIdUseCase byBusinessIdUseCase;

  public OrdersController(
      final FindOrderByIdUseCase byIdUseCase,
      final FindOrderByBusinessIdUseCase byBusinessIdUseCase) {
    this.byIdUseCase = byIdUseCase;
    this.byBusinessIdUseCase = byBusinessIdUseCase;
  }

  @GetMapping(value = "/v1/orders/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> findById(@PathVariable("id") final Long id) {
    final var result =
        byIdUseCase.invoke(new FindOrderByIdUseCase.Command(id)).map(OrderHttp::from);

    if (result.isEmpty()) {
      return orderNotFound(id);
    } else {
      return ResponseEntity.of(result);
    }
  }

  @GetMapping(
      value = "/v1/orders",
      params = {"businessId"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> findByBusinessId(@RequestParam(value = "businessId") final UUID businessId) {
    final var result = byBusinessIdUseCase
        .invoke(new FindOrderByBusinessIdUseCase.Command(businessId))
        .map(OrderHttp::from);

    if (result.isEmpty()) {
      return orderNotFound(businessId);
    } else {
      return ResponseEntity.of(result);
    }
  }
}

final class ProblemDetailOps {

  private ProblemDetailOps() {}

  static ResponseEntity<ProblemDetail> orderNotFound(final Long id) {
    return notFound(ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND, format("Order with id '%d' does not exist!", id)));
  }

  static ResponseEntity<ProblemDetail> orderNotFound(final UUID businessId) {
    return notFound(ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND, format("Order with businessId '%s' does not exist!", businessId)));
  }

  private static ResponseEntity<ProblemDetail> notFound(final ProblemDetail detail) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
  }
}
