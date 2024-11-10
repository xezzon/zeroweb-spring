package io.github.xezzon.geom.third_party_app;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.core.odata.ODataRequestParam;
import io.github.xezzon.geom.third_party_app.domain.AddThirdPartyAppReq;
import io.github.xezzon.geom.third_party_app.domain.ThirdPartyApp;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
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
  public Id add(@RequestBody AddThirdPartyAppReq req) {
    ThirdPartyApp thirdPartyApp = req.into();
    thirdPartyApp.setOwnerId(StpUtil.getLoginIdAsString());
    thirdPartyAppService.addThirdPartyApp(thirdPartyApp);
    return Id.of(thirdPartyApp.getId());
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
}
