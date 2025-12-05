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

package io.github.xezzon.zeroweb.role.constant;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.role.Role;

/// 角色静态常量定义
///
/// 提供系统中预定义的角色实例，
/// 用于代码中直接引用标准角色对象。
///
/// @author xezzon
public class RoleConstant {

  /// 超级管理员角色
  ///
  /// 系统预定义的超级管理员角色实例。
  /// 拥有系统最高权限，可以进行所有操作。
  /// 初始化时设置所有必要属性值。
  public static final Role ROOT = new Role();

  static {
    ROOT.setId("3");
    ROOT.setCode("ROOT");
    ROOT.setValue("ROOT");
    ROOT.setName("超级管理员");
    ROOT.setInheritable(false);
    ROOT.setParentId(DatabaseConstant.ROOT_ID);
  }

  private RoleConstant() {
  }
}
