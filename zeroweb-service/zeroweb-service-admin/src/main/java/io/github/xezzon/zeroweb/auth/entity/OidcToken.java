package io.github.xezzon.zeroweb.auth.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.github.xezzon.zeroweb.auth.AuthHttpConstant;
import lombok.Getter;
import lombok.Setter;

/**
 * 遵循OIDC规范的Token响应体
 * @author xezzon
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html">OIDC规范</a>
 */
@Getter
@Setter
@JsonNaming(SnakeCaseStrategy.class)
public class OidcToken {

  private String accessToken;
  private String refreshToken;
  /**
   * 过期时间 单位：秒
   */
  private Long expiresIn;
  private String idToken;

  public OidcToken(String accessToken, String idToken, Long expiresIn) {
    this.accessToken = accessToken;
    this.idToken = idToken;
    this.expiresIn = expiresIn;
  }

  @SuppressWarnings("unused")
  public String getTokenType() {
    return AuthHttpConstant.BEARER;
  }
}
