package io.github.xezzon.zeroweb.auth.repository;

import io.github.xezzon.zeroweb.auth.RoleUser;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleUserRepository extends
    JpaRepository<RoleUser, String>, JpaSpecificationExecutor<RoleUser> {

  List<RoleUser> findByRoleId(String roleId);

  boolean existsByRoleIdAndUserId(String roleId, String userId);

  @Transactional
  void deleteByRoleIdAndUserId(String roleId, String userId);

  List<RoleUser> findByUserId(String userId);
}
