package io.github.xezzon.zeroweb.auth.internal;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.github.xezzon.zeroweb.auth.event.UserLoginEvent;
import io.github.xezzon.zeroweb.auth.exception.InvalidPasswordException;
import io.github.xezzon.zeroweb.auth.util.SessionUtil;
import io.github.xezzon.zeroweb.crypto.JwtCryptoService;
import io.github.xezzon.zeroweb.user.IUserService4Auth;
import io.github.xezzon.zeroweb.user.User;
import jakarta.annotation.Resource;
import java.util.Objects;
import java.util.Set;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/// 认证服务
///
/// @author xezzon
@Service
public class AuthnService {

  private final IUserService4Auth userService;
  private final JwtCryptoService jwtCryptoService;
  @Resource
  private ApplicationEventPublisher eventPublisher;

  public AuthnService(
      final IUserService4Auth userService,
      final JwtCryptoService jwtCryptoService
  ) {
    this.userService = userService;
    this.jwtCryptoService = jwtCryptoService;
  }

  /// 校验用户名、口令
  ///
  /// 校验通过后将用户信息写入 Session
  ///
  /// @param username 用户名
  /// @param password 口令
  /// @throws InvalidPasswordException 用户不存在时抛出异常
  /// @throws InvalidPasswordException 用户名、密码不匹配时抛出异常
  protected void basicLogin(String username, String password) {
    final User user = userService.getUserByUsername(username);
    /* 校验用户名、口令 */
    if (user == null) {
      throw new InvalidPasswordException();
    }
    if (!BCrypt.checkpw(password, user.getCipher())) {
      throw new InvalidPasswordException();
    }
    /* 检查是否已存在会话 */
    if (StpUtil.isLogin()) {
      if (Objects.equals(StpUtil.getLoginIdAsString(), user.getId())) {
        // 原会话是同一个用户，则不作处理
        return;
      } else {
        // 原会话不是同一个用户，则需要将原会话作废
        StpUtil.logout();
      }
    }
    /* 写入 Session */
    StpUtil.login(user.getId());
    eventPublisher.publishEvent(UserLoginEvent.builder()
        .user(user)
        .build()
    );
  }

  /// 获取当前用户的认证信息
  ///
  /// @return 认证信息
  protected JwtClaim getCustomClaim() {
    final User user = SessionUtil.loadUser();
    final Set<String> roles = SessionUtil.loadRoles();
    final Set<String> permissions = SessionUtil.loadPermissions();
    return JwtClaim.newBuilder()
        .setSub(user.getId())
        .setPreferredUsername(user.getUsername())
        .setNickname(user.getNickname())
        .addAllRoles(roles)
        .addAllEntitlements(permissions)
        .build();
  }

  /// 生成并返回JWT（JSON Web Token）签名。
  ///
  /// JWT中包含认证信息
  ///
  /// @return 返回生成的JWT签名字符串
  protected String signJwt() {
    final JwtClaim claim = this.getCustomClaim();
    return jwtCryptoService.signJwt(claim);
  }

  /// 用户登录后，将用户信息加载到会话中
  ///
  /// @param event 用户登录事件
  @EventListener
  protected void listen(final UserLoginEvent event) {
    SessionUtil.saveUser(event.getUser());
  }
}
