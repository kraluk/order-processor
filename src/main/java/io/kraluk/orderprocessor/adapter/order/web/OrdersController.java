package io.kraluk.orderprocessor.adapter.order.web;

import static java.lang.String.format;

import io.kraluk.orderprocessor.shared.contract.http.OrderHttp;
import io.kraluk.orderprocessor.usecase.order.FindOrderByBusinessIdUseCase;
import io.kraluk.orderprocessor.usecase.order.FindOrderByIdUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders API")
@RestController
class OrdersController {

  private final OrdersControllerDelegate delegate;

  OrdersController(
      final FindOrderByIdUseCase byIdUseCase,
      final FindOrderByBusinessIdUseCase byBusinessIdUseCase) {
    this.delegate = new OrdersControllerDelegate(byIdUseCase, byBusinessIdUseCase);
  }

  @Operation(
      summary = "Find an Order by id",
      description = "Find an Order by id",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Order found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = OrderHttp.class),
                    examples =
                        @ExampleObject(
                            value =
                                """
                              {
                                "businessId": "f1b9b3b4-3b3b-4b3b-8b3b-3b3b3b3b3b3b",
                                "value": 100.0,
                                "currency": "USD",
                                "notes": "Order notes",
                                "version": 1,
                                "createdAt": "2021-09-01T00:00:00Z",
                                "updatedAt": "2021-09-01T00:00:00Z"
                              }
                              """))),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples =
                        @ExampleObject(
                            value =
                                """
                              {
                                "type": "about:blank",
                                "title": "Not Found",
                                "status": 404,
                                "detail": "Order with id '2' does not exist!",
                                "instance": "/v1/orders/2"
                              }
                              """)))
      })
  @GetMapping(value = "/v1/orders/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> findById(
      @Parameter(description = "Id of the Order", example = "34567") @PathVariable("id")
          final Long id) {
    return delegate.findById(id);
  }

  @Operation(
      summary = "Find an Order by businessId",
      description = "Find an Order by businessId",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Order found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = OrderHttp.class),
                    examples =
                        @ExampleObject(
                            value =
                                """
                              {
                                "businessId": "f1b9b3b4-3b3b-4b3b-8b3b-3b3b3b3b3b3b",
                                "value": 100.0,
                                "currency": "USD",
                                "notes": "Order notes",
                                "version": 1,
                                "createdAt": "2021-09-01T00:00:00Z",
                                "updatedAt": "2021-09-01T00:00:00Z"
                              }
                              """))),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples =
                        @ExampleObject(
                            value =
                                """
                              {
                                "type": "about:blank",
                                "title": "Not Found",
                                "status": 404,
                                "detail": "Order with businessId '50000000-0000-0000-0000-000000000000' does not exist!",
                                "instance": "/v1/orders"
                              }
                              """)))
      })
  @GetMapping(
      value = "/v1/orders",
      params = {"businessId"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> findByBusinessId(
      @Parameter(
              description = "Business id of the Order",
              example = "50000000-0000-0000-0000-000000000000")
          @RequestParam(value = "businessId")
          final UUID businessId) {
    return delegate.findByBusinessId(businessId);
  }
}

final class OrdersControllerDelegate {
  private static final Logger log = LoggerFactory.getLogger(OrdersControllerDelegate.class);

  private final FindOrderByIdUseCase byIdUseCase;
  private final FindOrderByBusinessIdUseCase byBusinessIdUseCase;

  OrdersControllerDelegate(
      final FindOrderByIdUseCase byIdUseCase,
      final FindOrderByBusinessIdUseCase byBusinessIdUseCase) {
    this.byIdUseCase = byIdUseCase;
    this.byBusinessIdUseCase = byBusinessIdUseCase;
  }

  ResponseEntity<?> findById(final Long id) {
    final var result =
        byIdUseCase.invoke(new FindOrderByIdUseCase.Command(id)).map(OrderHttp::from);

    if (result.isEmpty()) {
      log.debug("Order with id '{}' not found", id);
      return OrderProblem.notFoundOf(id);
    } else {
      return ResponseEntity.of(result);
    }
  }

  ResponseEntity<?> findByBusinessId(final UUID businessId) {
    final var result = byBusinessIdUseCase
        .invoke(new FindOrderByBusinessIdUseCase.Command(businessId))
        .map(OrderHttp::from);

    if (result.isEmpty()) {
      log.debug("Order with businessId '{}' not found", businessId);
      return OrderProblem.notFoundOf(businessId);
    } else {
      return ResponseEntity.of(result);
    }
  }
}

final class OrderProblem {

  private OrderProblem() {}

  static ResponseEntity<ProblemDetail> notFoundOf(final Long id) {
    return notFound(ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND, format("Order with id '%d' does not exist!", id)));
  }

  static ResponseEntity<ProblemDetail> notFoundOf(final UUID businessId) {
    return notFound(ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND, format("Order with businessId '%s' does not exist!", businessId)));
  }

  private static ResponseEntity<ProblemDetail> notFound(final ProblemDetail detail) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(detail);
  }
}
