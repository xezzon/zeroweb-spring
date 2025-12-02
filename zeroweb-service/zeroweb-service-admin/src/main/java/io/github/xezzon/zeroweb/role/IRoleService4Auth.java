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

package io.github.xezzon.zeroweb.role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/// @author xezzon
public interface IRoleService4Auth {

  /// 根据ID批量查询角色
  ///
  /// @param roleIds 角色ID
  /// @return 角色列表
  List<Role> findByIdIn(Collection<String> roleIds);

  /// 查询上级角色
  ///
  /// @param roleId 角色Id
  /// @return 上级角色
  Optional<Role> findParent(String roleId);

  /// 查询角色子级列表
  ///
  /// @param initial 角色ID
  /// @return 该角色的所有子级角色（包含递归子级，但不包括自身）
  List<Role> topDownList(Collection<String> initial);
}
