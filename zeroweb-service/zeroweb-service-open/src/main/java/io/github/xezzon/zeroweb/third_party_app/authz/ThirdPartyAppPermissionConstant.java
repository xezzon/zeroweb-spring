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

package io.github.xezzon.zeroweb.third_party_app.authz;

import io.github.xezzon.zeroweb.metadata.MenuInfo;
import io.github.xezzon.zeroweb.metadata.PermissionConstantUtil;
import java.util.List;

/// 第三方应用权限常量定义
///
/// 定义第三方应用相关的权限标识符和菜单信息
///
/// @author xezzon
public class ThirdPartyAppPermissionConstant {

  /// 邀请成员权限
  public static final String INVITE_MEMBER = "third-party-app:#:invite-member";
  /// 查看成员列表权限
  public static final String LIST_MEMBER = "third-party-app:#:list-member";
  /// 转移所有权权限
  public static final String MOVE_OWNERSHIP = "third-party-app:#:move-ownership";
  /// 查看第三方应用详情
  public static final String THIRD_PARTY_APP_READ = "third-party-app:#:read";
  /// 轮换访问密钥权限
  public static final String ROLL_ACCESS_SECRET = "third-party-app:#:roll-access-secret";

  /// 权限列表信息
  private static final List<MenuInfo> PERMISSIONS;

  static {
    PERMISSIONS = PermissionConstantUtil.read(ThirdPartyAppPermissionConstant.class);
  }

  /// 私有构造函数，防止实例化
  private ThirdPartyAppPermissionConstant() {
  }

  /// 获取权限列表
  ///
  /// @return 权限列表信息
  public static List<MenuInfo> getPermissions() {
    return PERMISSIONS;
  }
}
