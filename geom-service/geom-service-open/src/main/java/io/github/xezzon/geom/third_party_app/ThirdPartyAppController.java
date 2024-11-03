package io.github.xezzon.geom.third_party_app;

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
}
