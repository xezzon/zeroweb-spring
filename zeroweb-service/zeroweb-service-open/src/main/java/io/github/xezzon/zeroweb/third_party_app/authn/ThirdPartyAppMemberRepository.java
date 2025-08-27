package io.github.xezzon.zeroweb.third_party_app.authn;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/// @author xezzon
public interface ThirdPartyAppMemberRepository extends JpaRepository<ThirdPartyAppMember, String> {

  Optional<ThirdPartyAppMember> findByGroupIdAndUserId(String groupId, String userId);

  List<ThirdPartyAppMember> findByGroupIdOrderByCreateTimeDesc(String groupId);

  List<ThirdPartyAppMember> findByUserId(String userId);
}
