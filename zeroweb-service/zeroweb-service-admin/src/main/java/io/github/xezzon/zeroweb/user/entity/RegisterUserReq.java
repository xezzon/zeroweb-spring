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

package io.github.xezzon.zeroweb.user.entity;

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.user.User;
import jakarta.validation.constraints.Pattern;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 用户注册请求
///
/// 包含用户注册所需的必要信息，包括用户名、昵称和密码。
///
/// @param username 用户名
/// @param nickname 用户昵称
/// @param password 密码。为了防止身份被冒用，由用户设置的、只有用户自己知晓的口令。
/// @author xezzon
public record RegisterUserReq(
    @Pattern(
        regexp = "^\\w{3,32}$",
        message = "用户名必须是3~32位 小写字母/数字/下划线 组成的字符串"
    )
    String username,
    String nickname,
    @Pattern(
        regexp = "^(?!^\\d+$)(?!^[a-z]+$)(?!^[A-Z]+$)[\\x21-\\x7E]{8,}$",
        message = "密码由至少8位有效字符构成，且不允许是纯数字、纯小写或者纯大写字母"
    )
    String password
) implements Into<User> {

  /// 转换为用户实体
  ///
  /// @return 用户实体对象
  @Override
  public User into() {
    return Converter.INSTANCE.from(this);
  }

  /// 注册请求到用户的转换器
  ///
  /// @author xezzon
  @Mapper
  interface Converter extends From<RegisterUserReq, User> {

    /// 转换器实例
    Converter INSTANCE = Mappers.getMapper(Converter.class);

    /// 转换为用户实体
    ///
    /// @param registerUserReq 注册请求对象
    /// @return 用户实体对象
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cipher", ignore = true)
    @Override
    User from(RegisterUserReq registerUserReq);
  }
}
