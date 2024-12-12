package io.github.xezzon.zeroweb.common.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface TestEntityRepository extends
    JpaRepository<TestEntity, String>,
    JpaSpecificationExecutor<TestEntity> {

}
