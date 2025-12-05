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

package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.stp.StpInterface;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/// ZeroWeb 的 StpInterface 实现类，用于 Sa-Token 框架。
///
/// 该类通过从 JWT claims 中提取权限和角色信息，为 Sa-Token 提供认证授权服务。
///
/// @author xezzon
@Component
@SuppressWarnings("unused")
public class JwtStpInterface implements StpInterface {

  /// 获取指定账号的权限列表。
  ///
  /// 该方法从当前请求的 JWT claims 中提取权限列表。
  ///
  /// @param loginId 账号 id
  /// @param loginType 账号类型
  /// @return 权限列表，如果未找到则返回空列表
  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    return JwtAuth.get()
        .map(JwtClaim::getEntitlementsList)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
  }

  /// 获取指定账号的角色列表。
  ///
  /// 该方法从当前请求的 JWT claims 中提取角色列表。
  ///
  /// @param loginId 账号 id
  /// @param loginType 账号类型
  /// @return 角色列表，如果未找到则返回空列表
  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    return JwtAuth.get()
        .map(JwtClaim::getRolesList)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
  }
}
