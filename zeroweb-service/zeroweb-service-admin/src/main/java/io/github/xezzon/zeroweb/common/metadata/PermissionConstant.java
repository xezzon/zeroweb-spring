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

package io.github.xezzon.zeroweb.common.metadata;

import io.github.xezzon.zeroweb.metadata.MenuInfo;
import io.github.xezzon.zeroweb.metadata.PermissionConstantUtil;
import java.util.List;

/// 接口权限常量类
///
/// 定义系统中所有接口权限的标识符常量，用于权限控制和授权管理。
public final class PermissionConstant {

  /// 应用管理写权限
  public static final String APP_WRITE = "app:write";
  /// 授权管理读权限
  public static final String AUTHZ_READ = "authz:read";
  /// 角色用户关联权限
  public static final String AUTHZ_ROLE_USER = "authz:role-user";
  /// 角色权限关联权限
  public static final String AUTHZ_ROLE_PERMISSION = "authz:role-permission";
  /// 字典管理读权限
  public static final String DICT_READ = "dict:read";
  /// 字典管理写权限
  public static final String DICT_WRITE = "dict:write";
  /// 角色管理读权限
  public static final String ROLE_READ = "role:read";
  /// 角色管理写权限
  public static final String ROLE_WRITE = "role:write";
  /// 系统设置读权限
  public static final String SETTING_READ = "setting:read";
  /// 系统设置写权限
  public static final String SETTING_WRITE = "setting:write";

  /// 权限信息列表，从类的常量字段中解析生成
  private static final List<MenuInfo> PERMISSIONS;

  static {
    PERMISSIONS = PermissionConstantUtil.read(PermissionConstant.class);
  }

  /// 私有构造函数，防止实例化
  private PermissionConstant() {
  }

  /// 获取所有权限信息列表
  ///
  /// @return 权限信息列表
  public static List<MenuInfo> getPermissions() {
    return PERMISSIONS;
  }
}
