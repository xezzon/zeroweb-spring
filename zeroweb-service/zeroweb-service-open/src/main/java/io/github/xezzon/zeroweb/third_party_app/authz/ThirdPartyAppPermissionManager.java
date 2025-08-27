package io.github.xezzon.zeroweb.third_party_app.authz;

import static io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionConstant.LIST_MEMBER;

import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.third_party_app.IThirdPartyAppMemberService;
import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMember;
import java.util.Objects;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

/// @author xezzon
@Component
public class ThirdPartyAppPermissionManager {

  private final IThirdPartyAppMemberService thirdPartyAppMemberService;

  public ThirdPartyAppPermissionManager(IThirdPartyAppMemberService thirdPartyAppMemberService) {
    this.thirdPartyAppMemberService = thirdPartyAppMemberService;
  }

  public void check(String groupId, String userId, String permission) {
    Supplier<DataPermissionForbiddenException> thr = () ->
        new DataPermissionForbiddenException(groupId, userId, permission);
    ThirdPartyAppMember member = thirdPartyAppMemberService
        .queryMember(groupId, userId)
        .orElseThrow(thr);
    if (member.isOwner()) {
      // 所有者拥有该资源的所有权限
      return;
    }
    if (!Objects.equals(LIST_MEMBER, permission)) {
      throw thr.get();
    }
  }
}
