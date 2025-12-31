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

import static io.github.xezzon.zeroweb.crypto.constant.ZxcvbnConstant.ZXCVBN;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.secure.BCrypt;
import com.nulabinc.zxcvbn.Strength;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.core.odata.ODataRequestParam;
import io.github.xezzon.zeroweb.crypto.IPasswordService;
import io.github.xezzon.zeroweb.user.User;
import io.github.xezzon.zeroweb.user.entity.RegisterUserReq;
import jakarta.validation.Valid;
import java.util.Collections;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
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

  private final UserService userService;
  private final IPasswordService passwordService;

  /// 依赖注入
  ///
  /// @param userService 用户服务接口
  /// @param passwordService 口令服务
  UserHttpEndpoint(final UserService userService, final IPasswordService passwordService) {
    this.userService = userService;
    this.passwordService = passwordService;
  }

  /// 用户注册
  ///
  /// @param req 用户注册信息
  /// @return 用户 ID
  @PostMapping("/register")
  public Id register(@RequestBody @Valid final RegisterUserReq req) {
    // 计算并校验口令的强度
    Strength measure = ZXCVBN.measure(req.password(), Collections.singletonList(req.username()));
    passwordService.checkStrength(measure);
    // 将口令进行慢哈希，得到密码
    User user = req.into();
    String cipher = BCrypt.hashpw(req.password(), BCrypt.gensalt());
    user.setCipher(cipher);
    // 将用户保存到数据库
    userService.addUser(user);
    return Id.of(user.getId());
  }

  /// 获取用户列表
  /// @param odata 查询参数
  /// @return 包含分页信息的用户列表
  @GetMapping()
  @SaCheckPermission(PermissionConstant.USER_LIST)
  public Page<User> getUserPaged(final ODataRequestParam odata) {
    return userService.listAll(odata.into());
  }
}
