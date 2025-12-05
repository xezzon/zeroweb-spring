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
public final class PermissionConstant {

  /// 读取语言环境的权限标识
  public static final String LOCALE_READ = "locale:read";
  /// 写入语言环境的权限标识
  public static final String LOCALE_WRITE = "locale:write";

  private static final List<MenuInfo> PERMISSIONS;

  static {
    PERMISSIONS = PermissionConstantUtil.read(PermissionConstant.class);
  }

  /// 私有构造函数，防止实例化
  private PermissionConstant() {
  }

  /// 获取所有权限信息
  ///
  /// @return 权限信息列表
  public static List<MenuInfo> getPermissions() {
    return PERMISSIONS;
  }
}
