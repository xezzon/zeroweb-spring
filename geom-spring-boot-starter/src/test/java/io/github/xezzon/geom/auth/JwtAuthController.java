package io.github.xezzon.geom.auth;

import cn.dev33.satoken.annotation.SaCheckLogin;
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
    JwtClaim claim = JwtAuth.loadJwtClaim();
    return claim.getPreferredUsername();
  }
}
