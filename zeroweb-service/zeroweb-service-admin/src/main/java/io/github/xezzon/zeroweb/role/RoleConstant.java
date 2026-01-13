/*
 * SPDX-FileCopyrightText: Copyright (C) 2025-2026 xezzon
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

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;

/// 角色常量定义
///
/// 包含系统中常用的角色标识和ID常量，
/// 用于代码中的硬编码值统一管理。
///
/// @author xezzon
public final class RoleConstant {

  /// 系统管理员
  ///
  /// 系统默认的管理员角色编码，拥有系统最高权限。
  /// 此类角色通常用于系统运维和配置管理。
  public static final String ADMIN = "ADMIN";
  /// 系统管理员ID
  ///
  /// 系统管理员角色的默认ID值。
  /// 该值在数据库初始化时设置，具有特殊意义。
  public static final String ADMIN_ID = "1";

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
