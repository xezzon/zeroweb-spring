package io.github.xezzon.zeroweb.common.jpa;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class TestEntityDAO extends BaseDAO<TestEntity, String, TestEntityRepository> {

  TestEntityDAO(TestEntityRepository repository) {
    super(repository, TestEntity.class);
  }

  @Transactional
  public int updateField2ByField1(String field1, String field2) {
    return super.update((root, query, cb) -> query
        .set(TestEntity_.field2, field2)
        .where(cb.equal(root.get(TestEntity_.field1), field1))
    );
  }
}
