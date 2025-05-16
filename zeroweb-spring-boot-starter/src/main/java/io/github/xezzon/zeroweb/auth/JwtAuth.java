package io.github.xezzon.zeroweb.auth;

import java.util.Optional;

/**
 * JWT认证相关
 * @author xezzon
 */
public class JwtAuth {

  private static final ThreadLocal<JwtClaimWrapper> CLAIM = new InheritableThreadLocal<>();

  private JwtAuth() {
  }

  /**
   * 保存 Authorization 请求头中携带的 JWT
   * @param claimWrapper JWT对象
   */
  public static void save(JwtClaimWrapper claimWrapper) {
    CLAIM.set(claimWrapper);
  }

  /**
   * 获取当前认证信息。
   * 如果没获取到则返回 {@link Optional#empty()}
   * @return 当前认证信息
   */
  public static Optional<JwtClaimWrapper> get() {
    return Optional.ofNullable(CLAIM.get());
  }

  /**
   * 清楚 ThreadLocal 防止内存泄漏
   */
  public static void clear() {
    CLAIM.remove();
  }
}
