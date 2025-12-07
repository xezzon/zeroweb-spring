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

package io.github.xezzon.zeroweb.user;

import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.Nullable;

/// 用户服务向认证授权服务暴露的能力
/// @author xezzon
public interface IUserService4Auth {

  /// 根据用户名获取用户信息
  ///
  /// @param username 用户名
  /// @return 用户信息
  @Nullable User getUserByUsername(String username);

  /// 根据ID批量查询用户
  ///
  /// @param userIds 用户ID集合
  /// @return 用户列表
  List<User> findByIdIn(Collection<String> userIds);
}
