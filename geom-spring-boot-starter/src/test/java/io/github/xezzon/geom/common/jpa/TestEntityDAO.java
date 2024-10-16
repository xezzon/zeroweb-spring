package io.github.xezzon.geom.common.jpa;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class TestEntityDAO extends BaseDAO<TestEntity, String, TestEntityRepository> {

  protected TestEntityDAO(TestEntityRepository repository) {
    super(repository, TestEntity.class);
  }

  @Override
  public ICopier<TestEntity> getCopier() {
    return Copier.INSTANCE;
  }

  @Mapper
  interface Copier extends ICopier<TestEntity> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
