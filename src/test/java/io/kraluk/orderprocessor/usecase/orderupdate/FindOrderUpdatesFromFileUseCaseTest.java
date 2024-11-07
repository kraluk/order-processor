package io.kraluk.orderprocessor.usecase.orderupdate;

import static org.assertj.core.api.Assertions.assertThat;

import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdate;
import io.kraluk.orderprocessor.test.adapter.orderupdate.repository.InMemoryOrderUpdateRepository;
import io.kraluk.orderprocessor.test.domain.orderupdate.entity.TestOrderUpdateBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;

class FindOrderUpdatesFromFileUseCaseTest {

  private final InMemoryOrderUpdateRepository repository = new InMemoryOrderUpdateRepository();
  private final FindOrderUpdatesFromFileUseCase useCase =
      new FindOrderUpdatesFromFileUseCase(repository);

  @Test
  void shouldGetOrderUpdatesWhenTheyAreAvailableForGivenSource() {
    // Given
    final var source = "source1";
    final var update1 = TestOrderUpdateBuilder.buildRandom();
    final var update2 = TestOrderUpdateBuilder.buildRandom();

    // And given data are saved
    repository.save(source, List.of(update1, update2));

    // When
    final var result = useCase.invoke(FindOrderUpdatesFromFileUseCase.Command.of(source));

    // Then
    assertThat(result.toList())
        .hasSize(2)
        .extracting(OrderUpdate::getBusinessId)
        .contains(update1.getBusinessId(), update2.getBusinessId());
  }

  @Test
  void shouldNotGetAnyOrderUpdatesWhenTheyAreNotAvailableForGivenSource() {
    // Given
    final var source = "source1";

    // And given no data for given source

    // When
    final var result = useCase.invoke(FindOrderUpdatesFromFileUseCase.Command.of(source));

    // Then
    assertThat(result.toList()).isEmpty();
  }
}
