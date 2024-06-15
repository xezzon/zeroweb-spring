package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.dict.repository.DictRepository;
import io.github.xezzon.tao.trait.NewType;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class DictDAO implements NewType<DictRepository> {

  private final DictRepository repository;

  DictDAO(DictRepository repository) {
    this.repository = repository;
  }

  @Override
  public DictRepository get() {
    return this.repository;
  }
}
