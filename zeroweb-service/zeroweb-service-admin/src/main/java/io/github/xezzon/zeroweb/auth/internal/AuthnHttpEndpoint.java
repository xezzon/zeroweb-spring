package io.github.xezzon.zeroweb.auth.internal;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.BEARER;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.github.xezzon.zeroweb.auth.entity.BasicAuth;
import io.github.xezzon.zeroweb.auth.entity.OidcToken;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig.ZerowebJwtConfig;
import io.github.xezzon.zeroweb.crypto.internal.JwtKeyManager;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 认证管理
///
/// @author xezzon
@RequestMapping("/auth")
@RestController
public class AuthnHttpEndpoint {

  private final AuthnService authnService;
  private final ZerowebJwtConfig zerowebJwtConfig;
  @Resource
  private JwtKeyManager keyManager;

  public AuthnHttpEndpoint(final AuthnService authnService, final ZerowebConfig zerowebConfig) {
    this.authnService = authnService;
    this.zerowebJwtConfig = zerowebConfig.getJwt();
  }

  /// 用户名口令认证
  ///
  /// @param basicAuth 用户名、口令
  /// @return 令牌（即 Session ID）
  @PostMapping("/login/basic")
  public OidcToken basicLogin(@RequestBody final BasicAuth basicAuth) {
    authnService.basicLogin(basicAuth.username(), basicAuth.password());
    final String accessToken = StpUtil.getTokenValue();
    final Long expiredIn = StpUtil.getSessionTimeout();
    final String idToken = authnService.signJwt();
    return new OidcToken(accessToken, idToken, expiredIn);
  }

  /// @return 当前用户的认证信息
  @GetMapping("/self")
  public ResponseEntity<byte @NonNull []> self() throws InvalidProtocolBufferException {
    if (!StpUtil.isLogin()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT)
          .body(new byte[0]);
    }
    final String jwt = authnService.signJwt();
    final String publicKey = Base64.getEncoder()
        .encodeToString(keyManager.getPublicKey().getEncoded());
    final JwtClaim claim = authnService.getCustomClaim();
    final byte[] payload = JsonFormat.printer()
        .alwaysPrintFieldsWithNoPresence()
        .print(claim)
        .getBytes(StandardCharsets.UTF_8);
    return ResponseEntity.ok()
        .header(PUBLIC_KEY_HEADER, publicKey)
        .header(AUTHORIZATION, BEARER + " " + jwt)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(payload);
  }

  /// @return 用户令牌
  @SaCheckLogin
  @GetMapping("/token")
  public OidcToken getSsoToken(HttpServletResponse response) {
    final String accessToken = StpUtil.getTokenValue();
    final String idToken = authnService.signJwt();
    final Long expiredIn = zerowebJwtConfig.getTimeout();
    response.setHeader(PUBLIC_KEY_HEADER, Base64.getEncoder()
        .encodeToString(keyManager.getPublicKey().getEncoded())
    );
    response.setHeader(AUTHORIZATION, BEARER + " " + idToken);
    return new OidcToken(accessToken, idToken, expiredIn);
  }
}
