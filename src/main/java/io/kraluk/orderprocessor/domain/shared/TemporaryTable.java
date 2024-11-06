package io.kraluk.orderprocessor.domain.shared;

public interface TemporaryTable {
  String getName();

  final class DefaultTemporaryTable implements TemporaryTable {
    private final String name;

    private DefaultTemporaryTable(final String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }

    public static TemporaryTable of(final String name) {
      return new DefaultTemporaryTable(name);
    }
  }
}
