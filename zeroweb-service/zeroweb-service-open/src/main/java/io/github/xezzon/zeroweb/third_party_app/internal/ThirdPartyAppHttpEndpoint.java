package io.github.xezzon.zeroweb.third_party_app.internal;

import static io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionConstant.ROLL_ACCESS_SECRET;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.core.odata.ODataRequestParam;
import io.github.xezzon.zeroweb.third_party_app.AccessSecret;
import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionManager;
import io.github.xezzon.zeroweb.third_party_app.entity.AddThirdPartyAppReq;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 第三方应用管理
///
/// @author xezzon
@RestController
@RequestMapping("/third-party-app")
public class ThirdPartyAppHttpEndpoint {

  private final ThirdPartyAppService thirdPartyAppService;
  private final ThirdPartyAppPermissionManager thirdPartyAppPermissionManager;

  public ThirdPartyAppHttpEndpoint(
      final ThirdPartyAppService thirdPartyAppService,
      ThirdPartyAppPermissionManager thirdPartyAppPermissionManager
  ) {
    this.thirdPartyAppService = thirdPartyAppService;
    this.thirdPartyAppPermissionManager = thirdPartyAppPermissionManager;
  }

  /// 添加第三方应用
  ///
  /// @param req 请求体，包含要添加的第三方应用信息
  /// @return 添加成功后返回的第三方应用ID
  @PostMapping()
  public AccessSecret add(@RequestBody AddThirdPartyAppReq req) {
    ThirdPartyApp thirdPartyApp = req.into();
    thirdPartyApp.setOwnerId(JwtAuth.getOrThrow().getSub());
    return thirdPartyAppService.addThirdPartyApp(thirdPartyApp);
  }


  /// 获取当前用户的所有第三方应用列表
  ///
  /// @return 当前用户的所有第三方应用列表
  @GetMapping("/mine")
  public Page<ThirdPartyApp> listMyThirdPartyApp() {
    String userId = JwtAuth.getOrThrow().getSub();
    return thirdPartyAppService.listThirdPartyAppByUser(userId);
  }

  /// 查询所有第三方应用列表
  ///
  /// @param odata 查询参数
  /// @return 所有第三方应用列表
  @GetMapping()
  @SaCheckPermission({PermissionConstant.THIRD_PARTY_APP_READ})
  public Page<ThirdPartyApp> listThirdPartyApp(ODataRequestParam odata) {
    return thirdPartyAppService.listThirdPartyApp(odata.into());
  }

  /// 更新第三方应用的密钥
  ///
  /// @param appId 第三方应用ID
  /// @return 更新后的第三方应用的凭据与密钥
  @PatchMapping("/{appId}/roll")
  public AccessSecret rollAccessSecret(@PathVariable String appId) {
    thirdPartyAppPermissionManager.check(appId, JwtAuth.getOrThrow().getSub(), ROLL_ACCESS_SECRET);
    return thirdPartyAppService.rollAccessSecret(appId);
  }
}
