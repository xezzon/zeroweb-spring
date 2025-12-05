/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

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

/// `AuthnService` 是认证服务的核心业务逻辑处理组件。
///
/// 它负责用户身份验证、会话管理、JWT 签名以及处理用户登录事件。
///
/// @author xezzon
@Service
public class AuthnService {

  private final IUserService4Auth userService;
  private final JwtCryptoService jwtCryptoService;
  @Resource
  private ApplicationEventPublisher eventPublisher;

  /// 构造函数，注入用户服务和 JWT 加密服务。
  ///
  /// @param userService 用户服务接口实例。
  /// @param jwtCryptoService JWT 加密服务实例。
  public AuthnService(
      final IUserService4Auth userService,
      final JwtCryptoService jwtCryptoService
  ) {
    this.userService = userService;
    this.jwtCryptoService = jwtCryptoService;
  }

  /// 校验用户名和密码。
  ///
  /// 校验通过后，将用户信息写入 Sa-Token 会话，并发布 [UserLoginEvent]。
  /// 如果用户已登录且与当前用户相同，则不做处理；如果不同，则作废原会话。
  ///
  /// @param username 用户名。
  /// @param password 原始密码。
  /// @throws InvalidPasswordException 如果用户不存在或密码不匹配。
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

  /// 获取当前用户的认证信息，用于构建 JWT Claim。
  ///
  /// @return 包含用户 ID、用户名、昵称、角色和权限的 [JwtClaim] 对象。
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

  /// 生成并返回 JWT (JSON Web Token) 签名。
  ///
  /// JWT 中包含当前用户的认证信息。
  ///
  /// @return 返回生成的 JWT 签名字符串。
  protected String signJwt() {
    final JwtClaim claim = this.getCustomClaim();
    return jwtCryptoService.signJwt(claim);
  }

  /// 监听用户登录事件，将用户信息加载到会话中。
  ///
  /// @param event 用户登录事件 [UserLoginEvent]。
  @EventListener
  protected void listen(final UserLoginEvent event) {
    SessionUtil.saveUser(event.getUser());
  }
}
