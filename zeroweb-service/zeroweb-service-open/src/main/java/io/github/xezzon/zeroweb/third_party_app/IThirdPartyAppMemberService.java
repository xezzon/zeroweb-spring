package io.github.xezzon.zeroweb.third_party_app;

import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMember;
import java.util.Optional;

/// @author xezzon
public interface IThirdPartyAppMemberService {

  /// 查询指定用户在指定用户组的成员身份
  ///
  /// @param groupId 用户组ID
  /// @param userId 用户ID
  /// @return 用户组成员
  Optional<ThirdPartyAppMember> queryMember(String groupId, String userId);
}
