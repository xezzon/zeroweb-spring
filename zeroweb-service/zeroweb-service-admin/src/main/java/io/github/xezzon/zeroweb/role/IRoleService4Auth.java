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

/// 角色服务接口 - 认证模块专用
///
/// 该接口为认证模块提供角色相关的查询服务，包含角色继承关系查询等功能。
/// 接口方法主要用于权限验证和角色层级关系处理。
///
/// @author xezzon
public interface IRoleService4Auth {

  /// 根据ID批量查询角色
  ///
  /// @param roleIds 角色ID集合，不能为空
  /// @return 角色列表，按照传入的ID顺序返回，如果角色不存在则跳过
  List<Role> findByIdIn(Collection<String> roleIds);

  /// 查询上级角色
  ///
  /// @param roleId 角色ID，不能为空
  /// @return 上级角色，如果角色不存在或为根节点则返回空
  Optional<Role> findParent(String roleId);

  /// 查询角色子级列表
  ///
  /// @param initial 初始角色ID集合，从这些角色开始向下查找
  /// @return 该角色的所有子级角色（包含递归子级，但不包括自身），按层级排序
  List<Role> topDownList(Collection<String> initial);
}
