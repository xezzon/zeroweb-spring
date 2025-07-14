package io.github.xezzon.zeroweb.third_party_app.auth;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public interface ThirdPartyAppMemberRepository extends JpaRepository<ThirdPartyAppMember, String> {

  void deleteByRoleId(String roleId);

  List<ThirdPartyAppMember> findByRoleId(String roleId);

  List<ThirdPartyAppMember> findByGroupId(String groupId);

  List<ThirdPartyAppMember> findByUserId(String userId);

  void deleteByGroupIdAndUserId(String groupId, String userId);
}
