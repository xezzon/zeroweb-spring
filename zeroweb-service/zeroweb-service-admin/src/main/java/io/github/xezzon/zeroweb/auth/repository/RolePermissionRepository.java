package io.github.xezzon.zeroweb.auth.repository;

import io.github.xezzon.zeroweb.auth.domain.RolePermission;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends
    JpaRepository<RolePermission, String>, JpaSpecificationExecutor<RolePermission> {

  List<RolePermission> findByRoleIdIn(Collection<String> roleIds);

  boolean existsByRoleIdAndPermission(String roleId, String permission);

  @Transactional
  long deleteByRoleIdInAndPermission(Collection<String> roleIds, String permission);

  List<RolePermission> findByPermission(String permission);
}
