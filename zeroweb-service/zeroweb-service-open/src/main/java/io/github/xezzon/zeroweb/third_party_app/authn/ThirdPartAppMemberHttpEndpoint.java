package io.github.xezzon.zeroweb.third_party_app.authn;

import io.github.xezzon.zeroweb.common.domain.Id;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
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

  public ThirdPartAppMemberHttpEndpoint(ThirdPartyAppMemberService thirdPartyAppMemberService) {
    this.thirdPartyAppMemberService = thirdPartyAppMemberService;
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
    // TODO: 校验资源权限
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
   * 查询制定第三方应用的成员
   * @param appId 第三方应用ID
   */
  @GetMapping("/third-party-app/{appId}/member")
  public List<ThirdPartyAppMember> listMember(@PathVariable String appId) {
    // TODO: 校验资源权限
    return thirdPartyAppMemberService.listMember(appId);
  }
}
