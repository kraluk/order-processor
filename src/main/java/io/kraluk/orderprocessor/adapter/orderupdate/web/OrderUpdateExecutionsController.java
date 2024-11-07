package io.kraluk.orderprocessor.adapter.orderupdate.web;

import io.kraluk.orderprocessor.adapter.orchestrator.OrderUpdatesOrchestrator;
import io.kraluk.orderprocessor.shared.contract.http.OrderUpdateExecutionHttp;
import java.time.Clock;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class OrderUpdateExecutionsController {

  private final OrderUpdatesOrchestrator orchestrator;
  private final Clock clock;

  OrderUpdateExecutionsController(final OrderUpdatesOrchestrator orchestrator, final Clock clock) {
    this.orchestrator = orchestrator;
    this.clock = clock;
  }

  @PostMapping(
      value = "/v1/orders/update-executions/{source}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> initiateProcess(@PathVariable("source") final String source) {
    orchestrator.process(source);

    return ResponseEntity.accepted()
        .body(new OrderUpdateExecutionHttp(
            source, "Order update process has been accepted.", clock.instant()));
  }
}
