package io.github.xezzon.zeroweb.third_party_app.auth;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface ThirdPartyAppRoleRepository extends JpaRepository<ThirdPartyAppRole, String> {

  List<ThirdPartyAppRole> findByGroupId(String groupId);

  @Query("SELECT DISTINCT p FROM ThirdPartyAppRole r JOIN r.permissions p WHERE r.id IN :roleIds")
  List<String> findPermissionsByRoleIds(Collection<String> roleIds);
}
