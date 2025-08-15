package io.github.xezzon.zeroweb.third_party_app.authn;

import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionConstant;
import io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionManager;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController
public class ThirdPartAppMemberHttpEndpoint {

  private final ThirdPartyAppMemberService thirdPartyAppMemberService;
  private final ThirdPartyAppPermissionManager thirdPartyAppPermissionManager;

  public ThirdPartAppMemberHttpEndpoint(
      ThirdPartyAppMemberService thirdPartyAppMemberService,
      ThirdPartyAppPermissionManager thirdPartyAppPermissionManager
  ) {
    this.thirdPartyAppMemberService = thirdPartyAppMemberService;
    this.thirdPartyAppPermissionManager = thirdPartyAppPermissionManager;
  }

  /**
   * 邀请人员成为应用的成员。
   * @param appId 应用ID
   * @param userId 用户ID。如果为空，则生成对所有人有效的邀请码
   * @param timeout 邀请码有效期。单位：小时。默认为24。
   * @return 邀请码
   */
  @PostMapping("/third-party-app/{appId}/member")
  public String inviteMember(
      @PathVariable String appId,
      @RequestParam(required = false) String userId,
      @RequestParam(required = false, defaultValue = "24") int timeout
  ) {
    thirdPartyAppPermissionManager
        .check(appId, JwtAuth.getOrThrow().getSub(), ThirdPartyAppPermissionConstant.INVITE_MEMBER);
    return thirdPartyAppMemberService.inviteMember(appId, userId, timeout);
  }

  /**
   * 持邀请码加入应用
   * @param token 邀请码
   * @return 成员ID
   */
  @PutMapping("/third-party-app/-/member")
  public Id acceptInvitation(@RequestParam String token) {
    String id = thirdPartyAppMemberService.acceptInvitation(token);
    return Id.of(id);
  }

  /**
   * 查询第三方应用的成员
   * @param appId 第三方应用ID
   */
  @GetMapping("/third-party-app/{appId}/member")
  public List<ThirdPartyAppMember> listMember(@PathVariable String appId) {
    thirdPartyAppPermissionManager
        .check(appId, JwtAuth.getOrThrow().getSub(), ThirdPartyAppPermissionConstant.LIST_MEMBER);
    return thirdPartyAppMemberService.listMember(appId);
  }

  /**
   * 第三方应用所有权转移
   * @param userId 转移的目标用户
   */
  @PatchMapping("/third-party-app/{appId}/owner")
  public void moveOwnership(@PathVariable String appId, @RequestParam String userId) {
    thirdPartyAppMemberService.moveOwnership(appId, userId);
  }
}
