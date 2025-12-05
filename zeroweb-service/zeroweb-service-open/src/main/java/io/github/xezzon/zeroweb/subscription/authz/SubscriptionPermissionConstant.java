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

package io.github.xezzon.zeroweb.subscription.authz;

import io.github.xezzon.zeroweb.metadata.MenuInfo;
import io.github.xezzon.zeroweb.metadata.PermissionConstantUtil;
import java.util.List;

/// 订阅权限常量类，定义了订阅相关的权限标识
/// @author xezzon
public class SubscriptionPermissionConstant {

  /// 订阅权限：添加订阅
  public static final String SUBSCRIBE = "subscription:#:add";
  /// 订阅权限：查询订阅列表
  public static final String LIST_SUBSCRIPTION = "subscription:#:read";

  private static final List<MenuInfo> PERMISSIONS;

  static {
    PERMISSIONS = PermissionConstantUtil.read(SubscriptionPermissionConstant.class);
  }

  /// 私有构造器，防止实例化
  private SubscriptionPermissionConstant() {
  }

  /// 获取所有订阅权限列表
  /// @return 权限信息列表
  public static List<MenuInfo> getPermissions() {
    return PERMISSIONS;
  }
}
