package io.github.xezzon.zeroweb.subscription.authz;

import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.third_party_app.IThirdPartyAppMemberService;
import org.springframework.stereotype.Component;

/// @author xezzon
@Component
public class SubscriptionPermissionManager {

  private final IThirdPartyAppMemberService thirdPartyAppMemberService;

  public SubscriptionPermissionManager(IThirdPartyAppMemberService thirdPartyAppMemberService) {
    this.thirdPartyAppMemberService = thirdPartyAppMemberService;
  }

  public void check(String groupId, String userId, String permission) {
    thirdPartyAppMemberService
        .queryMember(groupId, userId)
        .orElseThrow(() -> new DataPermissionForbiddenException(groupId, userId, permission));
  }
}
