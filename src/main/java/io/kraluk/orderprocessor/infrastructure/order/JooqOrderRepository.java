package io.kraluk.orderprocessor.infrastructure.order;

import io.kraluk.orderprocessor.domain.order.OrderRepository;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class JooqOrderRepository implements OrderRepository {
  private static final Logger log = LoggerFactory.getLogger(JooqOrderRepository.class);

  private final DSLContext dsl;

  public JooqOrderRepository(DSLContext dsl) {
    this.dsl = dsl;
  }
}
