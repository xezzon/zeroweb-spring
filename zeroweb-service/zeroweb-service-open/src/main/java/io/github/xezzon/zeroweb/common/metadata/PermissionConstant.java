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

/// 权限常量定义。
/// 
/// 定义系统中使用的各种权限标识符。
/// @author xezzon
public class PermissionConstant {

  /// OpenAPI写入权限
  /// 
  /// 允许写入OpenAPI相关配置和数据
  public static final String OPENAPI_WRITE = "openapi:write";
  /// OpenAPI发布权限
  /// 
  /// 允许发布OpenAPI配置
  public static final String OPENAPI_PUBLISH = "openapi:publish";
  /// 订阅审核权限
  /// 
  /// 允许对订阅服务进行审核操作
  public static final String SUBSCRIPTION_AUDIT = "subscription:audit";
  /// 第三方应用读取权限
  /// 
  /// 允许读取第三方应用相关信息
  public static final String THIRD_PARTY_APP_READ = "third-party-app:read";

  /// 权限信息列表
  /// 
  /// 存储所有权限的菜单信息对象
  private static final List<MenuInfo> PERMISSIONS;

  static {
    PERMISSIONS = PermissionConstantUtil.read(PermissionConstant.class);
  }

  /// 私有构造器
  /// 
  /// 防止实例化工具类
  private PermissionConstant() {
  }

  /// 获取权限列表
  /// 
  /// @return 包含所有权限信息的列表
  public static List<MenuInfo> getPermissions() {
    return PERMISSIONS;
  }
}
