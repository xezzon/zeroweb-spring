package io.github.xezzon.zeroweb.auth;

/**
 * HTTP 认证相关的常量
 * @author xezzon
 */
public final class AuthHttpConstant {

  private AuthHttpConstant() {
  }

  /**
   * HTTP "Authentication" 请求头
   * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Reference/Headers/Authorization">Authorization - HTTP | MDN</a>
   */
  public static final String AUTHORIZATION = "Authorization";

  /**
   * HTTP "Bearer" 身份验证方案
   * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Guides/Authentication#bearer">HTTP 身份验证 - HTTP | MDN</a>
   */
  public static final String BEARER = "Bearer";
}
