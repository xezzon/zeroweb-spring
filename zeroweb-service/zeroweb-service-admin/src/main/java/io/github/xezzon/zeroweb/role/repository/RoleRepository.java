package io.github.xezzon.zeroweb.role.repository;

import io.github.xezzon.zeroweb.role.domain.Role;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface RoleRepository extends
    JpaRepository<Role, String>,
    JpaSpecificationExecutor<Role> {

  Optional<Role> findByValue(String value);

  List<Role> findByParentIdIn(Collection<String> parentIds);
}
