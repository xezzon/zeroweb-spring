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

package io.github.xezzon.zeroweb.user.internal;

import cn.dev33.satoken.secure.BCrypt;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.user.User;
import io.github.xezzon.zeroweb.user.entity.RegisterUserReq;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 用户管理
///
/// @author xezzon
@RequestMapping("/user")
@RestController
public class UserHttpEndpoint {

  /// 用户服务接口
  private final UserService userService;

  /// 依赖注入
  ///
  /// @param userService 用户服务接口
  UserHttpEndpoint(final UserService userService) {
    this.userService = userService;
  }

  /// 用户注册
  ///
  /// 处理用户注册请求，创建新的用户账号。
  ///
  /// @param req 用户注册信息
  /// @return 用户ID
  @PostMapping("/register")
  public Id register(@RequestBody @Validated RegisterUserReq req) {
    User user = req.into();
    String cipher = BCrypt.hashpw(req.password(), BCrypt.gensalt());
    user.setCipher(cipher);
    userService.addUser(user);
    return Id.of(user.getId());
  }
}
