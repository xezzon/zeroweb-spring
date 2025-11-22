package io.github.xezzon.zeroweb.third_party_app.authn;

import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/// @author xezzon
@Repository
@NullMarked
public interface ThirdPartyAppMemberRepository extends JpaRepository<ThirdPartyAppMember, String> {

  Optional<ThirdPartyAppMember> findByGroupIdAndUserId(String groupId, String userId);

  List<ThirdPartyAppMember> findByGroupIdOrderByCreateTimeDesc(String groupId);

  List<ThirdPartyAppMember> findByUserId(String userId);
}
