package io.github.xezzon.zeroweb.openapi.repository;

import io.github.xezzon.zeroweb.openapi.domain.Openapi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface OpenapiRepository extends
    JpaRepository<Openapi, String>,
    JpaSpecificationExecutor<Openapi> {

  Openapi findByCode(String code);
}
