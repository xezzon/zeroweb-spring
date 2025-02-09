package io.github.xezzon.zeroweb.app.repository;

import io.github.xezzon.zeroweb.app.domain.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface AppRepository extends
    JpaRepository<App, String>,
    JpaSpecificationExecutor<App> {

}
