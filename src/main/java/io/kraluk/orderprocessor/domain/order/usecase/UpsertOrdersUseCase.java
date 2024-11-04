package io.kraluk.orderprocessor.domain.order.usecase;

import io.kraluk.orderprocessor.domain.order.Order;

import java.util.stream.Stream;

public interface UpsertOrdersUseCase {

  int invoke(Command command);

  record Command(Stream<Order> orders) {
  }
}

