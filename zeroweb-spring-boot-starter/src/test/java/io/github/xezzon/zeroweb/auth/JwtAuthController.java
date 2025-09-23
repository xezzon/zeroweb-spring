package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController
@RequestMapping("/jwt")
public class JwtAuthController {

  @GetMapping()
  @SaCheckLogin
  public String getClaim() {
    final JwtClaim claimWrapper = JwtAuth.get().orElseThrow();
    StpUtil.checkRole("test");
    StpUtil.checkPermission(RandomUtil.randomString(8));
    return claimWrapper.getPreferredUsername();
  }
}
