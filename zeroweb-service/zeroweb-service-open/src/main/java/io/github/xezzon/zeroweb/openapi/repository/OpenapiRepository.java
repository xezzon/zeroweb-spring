package io.github.xezzon.zeroweb.openapi.repository;

import io.github.xezzon.zeroweb.openapi.Openapi;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// @author xezzon
@Repository
@NullMarked
public interface OpenapiRepository extends
    JpaRepository<Openapi, String>,
    JpaSpecificationExecutor<Openapi> {

  Optional<Openapi> findByCode(String code);
}
