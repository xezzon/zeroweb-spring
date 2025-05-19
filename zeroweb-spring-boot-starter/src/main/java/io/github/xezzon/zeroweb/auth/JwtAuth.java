package io.github.xezzon.zeroweb.auth;

import static cn.dev33.satoken.exception.NotLoginException.DEFAULT_MESSAGE;
import static cn.dev33.satoken.exception.NotLoginException.NOT_TOKEN;

import cn.dev33.satoken.exception.NotLoginException;
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
  public static void save(final JwtClaimWrapper claimWrapper) {
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
   * 获取当前认证信息。
   * @return 认证信息
   * @throws NotLoginException 没有认证
   */
  public static JwtClaimWrapper getOrThrow() {
    return get()
        .orElseThrow(() ->
            new NotLoginException(DEFAULT_MESSAGE, null, NOT_TOKEN)
        );
  }

  /**
   * 清楚 ThreadLocal 防止内存泄漏
   */
  public static void clear() {
    CLAIM.remove();
  }
}
