package io.github.xezzon.zeroweb.auth;

import static cn.dev33.satoken.exception.NotLoginException.DEFAULT_MESSAGE;
import static cn.dev33.satoken.exception.NotLoginException.NOT_TOKEN;

import cn.dev33.satoken.exception.NotLoginException;
import io.grpc.Context;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

/**
 * JWT认证相关
 * @author xezzon
 */
public class JwtAuth {

  public static final ScopedValue<JwtClaim> CLAIM = ScopedValue.newInstance();
  public static final Context.Key<JwtClaim> CONTEXT = Context.key("JwtClaim");

  private JwtAuth() {
  }

  /**
   * 获取当前认证信息。
   * 如果没获取到则返回 {@link Optional#empty()}
   * @return 当前认证信息
   */
  public static @NonNull Optional<JwtClaim> get() {
    JwtClaim grpcContext = CONTEXT.get();
    if (grpcContext != null) {
      return Optional.of(grpcContext);
    }
    if (!CLAIM.isBound()) {
      return Optional.empty();
    }
    return Optional.ofNullable(CLAIM.get());
  }

  /**
   * 获取当前认证信息。
   * @return 认证信息
   * @throws NotLoginException 没有认证
   */
  public static @NonNull JwtClaim getOrThrow() {
    return get()
        .orElseThrow(() ->
            new NotLoginException(DEFAULT_MESSAGE, null, NOT_TOKEN)
        );
  }
}
