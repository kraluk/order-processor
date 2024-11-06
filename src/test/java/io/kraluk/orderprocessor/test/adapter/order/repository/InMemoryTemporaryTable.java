package io.kraluk.orderprocessor.test.adapter.order.repository;

import io.kraluk.orderprocessor.domain.shared.TemporaryTable;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryTemporaryTable<T> implements TemporaryTable {

  private final String name;
  private final List<T> data;

  public InMemoryTemporaryTable(final String name, final List<T> data) {
    this.name = name;
    this.data = new ArrayList<>(data);
  }

  @Override
  public String getName() {
    return name;
  }

  public List<T> getData() {
    return data;
  }
}