package io.github.xezzon.zeroweb.common.jpa;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
@NullMarked
public interface TestEntityRepository extends
    JpaRepository<TestEntity, String>,
    JpaSpecificationExecutor<TestEntity> {

}
