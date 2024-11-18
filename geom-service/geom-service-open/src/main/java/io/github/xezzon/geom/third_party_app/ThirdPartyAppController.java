package io.github.xezzon.geom.third_party_app;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.geom.core.odata.ODataRequestParam;
import io.github.xezzon.geom.subscription.domain.Subscription;
import io.github.xezzon.geom.third_party_app.domain.AccessSecret;
import io.github.xezzon.geom.third_party_app.domain.AddThirdPartyAppReq;
import io.github.xezzon.geom.third_party_app.domain.ThirdPartyApp;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第三方应用管理
 * @author xezzon
 */
@RestController
@RequestMapping("/third-party-app")
public class ThirdPartyAppController {

  private final ThirdPartyAppService thirdPartyAppService;

  public ThirdPartyAppController(ThirdPartyAppService thirdPartyAppService) {
    this.thirdPartyAppService = thirdPartyAppService;
  }

  /**
   * 添加第三方应用
   * @param req 请求体，包含要添加的第三方应用信息
   * @return 添加成功后返回的第三方应用ID
   */
  @PostMapping("/add")
  public AccessSecret add(@RequestBody AddThirdPartyAppReq req) {
    ThirdPartyApp thirdPartyApp = req.into();
    thirdPartyApp.setOwnerId(StpUtil.getLoginIdAsString());
    return thirdPartyAppService.addThirdPartyApp(thirdPartyApp);
  }


  /**
   * 获取当前用户的所有第三方应用列表
   * @return 当前用户的所有第三方应用列表
   */
  @GetMapping("/mine")
  public Page<ThirdPartyApp> listMyThirdPartyApp(ODataRequestParam odata) {
    String userId = StpUtil.getLoginIdAsString();
    return thirdPartyAppService.listThirdPartyAppByUser(odata.into(), userId);
  }

  /**
   * 查询所有第三方应用列表
   * @param odata 查询参数
   * @return 所有第三方应用列表
   */
  @GetMapping()
  public Page<ThirdPartyApp> listThirdPartyApp(ODataRequestParam odata) {
    return thirdPartyAppService.listThirdPartyApp(odata.into());
  }

  /**
   * 查询所有已发布的对外接口以及指定第三方应用的订阅情况
   * @param odata 查询参数
   * @param appId 第三方应用ID
   * @return 所有已发布的对外接口以及指定第三方应用的订阅情况
   */
  @GetMapping("/{appId}/subscription")
  public Page<Subscription> listSubscription(ODataRequestParam odata, @PathVariable String appId) {
    return thirdPartyAppService.listSubscription(odata.into(), appId);
  }

  /**
   * 更新第三方应用的密钥
   * @param appId 第三方应用ID
   * @return 更新后的第三方应用的凭据与密钥
   */
  @PatchMapping("/{appId}/roll")
  public AccessSecret rollAccessSecret(@PathVariable String appId) {
    return thirdPartyAppService.rollAccessSecret(appId);
  }
}
