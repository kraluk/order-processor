package io.kraluk.orderprocessor.adapter.orderupdate.web;

import io.kraluk.orderprocessor.adapter.orchestrator.OrderUpdatesOrchestrator;
import io.kraluk.orderprocessor.shared.contract.http.OrderHttp;
import io.kraluk.orderprocessor.shared.contract.http.OrderUpdateExecutionHttp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;

@Tag(name = "Order Update Executions API")
@RestController
class OrderUpdateExecutionsController {

  private final OrderUpdateExecutionsControllerDelegate delegate;

  OrderUpdateExecutionsController(final OrderUpdatesOrchestrator orchestrator, final Clock clock) {
    this.delegate = new OrderUpdateExecutionsControllerDelegate(orchestrator, clock);
  }

  @Operation(
      summary = "Executes the Order update process",
      description = "Executes the Order update process",
      responses = {
          @ApiResponse(
              responseCode = "202",
              description = "Accepted request of the Order update process's start",
              content =
              @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = OrderHttp.class),
                  examples =
                  @ExampleObject(
                      value =
                          """
                              {
                                "source": "orders.csv",
                                "message": "Order update process has been accepted.",
                                "timestamp": "2024-11-08T21:17:51.033349Z"
                              }
                              """)))
      })
  @PostMapping(
      value = "/v1/orders/update-executions/{source}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> initiateProcess(
      @Parameter(
          description = "Source (file name) of the Order to be processed",
          example = "orders.csv")
      @PathVariable("source") final String source) {
    return delegate.initiateProcess(source);
  }
}

final class OrderUpdateExecutionsControllerDelegate {
  private static final Logger log = LoggerFactory.getLogger(OrderUpdateExecutionsControllerDelegate.class);

  private final OrderUpdatesOrchestrator orchestrator;
  private final Clock clock;

  OrderUpdateExecutionsControllerDelegate(OrderUpdatesOrchestrator orchestrator, Clock clock) {
    this.orchestrator = orchestrator;
    this.clock = clock;
  }

  ResponseEntity<?> initiateProcess(final String source) {
    orchestrator.process(source);
    log.debug("Order Update has been executed for the source - '{}'", source);

    return ResponseEntity.accepted()
        .body(new OrderUpdateExecutionHttp(
            source, "Order update process has been accepted.", clock.instant()));
  }
}