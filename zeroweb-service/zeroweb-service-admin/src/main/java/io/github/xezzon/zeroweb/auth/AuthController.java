package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.auth.entity.BasicAuth;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证授权管理
 * @author xezzon
 */
@RequestMapping("/auth")
@RestController
public class AuthController {

  private final AuthNService authNService;

  public AuthController(AuthNService authNService) {
    this.authNService = authNService;
  }

  /**
   * 用户名口令认证
   * @param basicAuth 用户名、口令
   * @return 令牌（即 Session ID）
   */
  @PostMapping("/login/basic")
  public SaTokenInfo basicLogin(@RequestBody BasicAuth basicAuth) {
    authNService.basicLogin(basicAuth.username(), basicAuth.password());
    return StpUtil.getTokenInfo();
  }

  /**
   * 单点登录
   * @return JWT
   */
  @GetMapping("/sso")
  public SaTokenInfo sso() {
    String tokenValue = authNService.signJwt();
    SaTokenInfo saTokenInfo = new SaTokenInfo();
    saTokenInfo.setTokenName(HttpHeaders.AUTHORIZATION);
    saTokenInfo.setTokenValue(tokenValue);
    return saTokenInfo;
  }
}
