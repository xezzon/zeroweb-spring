package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.auth.entity.BasicAuth;
import io.github.xezzon.zeroweb.auth.entity.OidcToken;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig.ZerowebJwtConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证管理
 * @author xezzon
 */
@RequestMapping("/auth")
@RestController
public class AuthnController {

  private final AuthnService authnService;
  private final ZerowebJwtConfig zerowebJwtConfig;

  public AuthnController(AuthnService authnService, ZerowebConfig zerowebConfig) {
    this.authnService = authnService;
    this.zerowebJwtConfig = zerowebConfig.getJwt();
  }

  /**
   * 用户名口令认证
   * @param basicAuth 用户名、口令
   * @return 令牌（即 Session ID）
   */
  @PostMapping("/login/basic")
  public OidcToken basicLogin(@RequestBody BasicAuth basicAuth) {
    authnService.basicLogin(basicAuth.username(), basicAuth.password());
    final String accessToken = StpUtil.getTokenValue();
    final Long expiredIn = StpUtil.getSessionTimeout();
    final String idToken = authnService.signJwt();
    return new OidcToken(accessToken, idToken, expiredIn);
  }

  @GetMapping("/token")
  public OidcToken getSsoToken() {
    final String accessToken = StpUtil.getTokenValue();
    final String idToken = authnService.signJwt();
    final Long expiredIn = zerowebJwtConfig.getTimeout();
    return new OidcToken(accessToken, idToken, expiredIn);
  }
}
