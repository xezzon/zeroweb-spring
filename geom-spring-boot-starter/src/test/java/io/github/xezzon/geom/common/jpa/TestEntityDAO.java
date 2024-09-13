package io.github.xezzon.geom.common.jpa;

import com.querydsl.core.types.dsl.EntityPathBase;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class TestEntityDAO extends BaseDAO<TestEntity, String, TestEntityRepository> {

  protected TestEntityDAO(TestEntityRepository repository) {
    super(repository);
  }

  @Override
  public ICopier<TestEntity> getCopier() {
    return Copier.INSTANCE;
  }

  @Override
  protected EntityPathBase<TestEntity> getQuery() {
    return QTestEntity.testEntity;
  }

  @Mapper
  interface Copier extends ICopier<TestEntity> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
