package io.kraluk.orderprocessor.adapter.orderupdate.repository;

import io.kraluk.orderprocessor.domain.orderupdate.entity.OrderUpdate;
import io.kraluk.orderprocessor.domain.orderupdate.port.OrderUpdateRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
class DefaultOrderUpdateRepository implements OrderUpdateRepository {

  private final OrderUpdateDownloader downloader;
  private final OrderUpdateParser parser;

  DefaultOrderUpdateRepository(final OrderUpdateDownloader downloader, final OrderUpdateParser parser) {
    this.downloader = downloader;
    this.parser = parser;
  }

  // FEATURE: potentially downloading data to a local storage is not needed, as we can stream the data directly from the remote location,
  // but in this approach we can face network issues during the processing of the data
  @Override
  public Stream<OrderUpdate> findAllFrom(final String source) {
    return downloader.download(source)
        .stream()
        .flatMap(parser::parse);
  }
}
