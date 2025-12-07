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

import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.third_party_app.IThirdPartyAppMemberService;
import org.springframework.stereotype.Component;

/// 订阅权限管理器，用于校验用户对订阅功能的访问权限
/// @author xezzon
@Component
public class SubscriptionPermissionManager {

  /// 第三方应用成员服务，用于校验用户权限
  private final IThirdPartyAppMemberService thirdPartyAppMemberService;

  /// 构造器注入第三方应用成员服务
  /// @param thirdPartyAppMemberService 第三方应用成员服务实例
  public SubscriptionPermissionManager(IThirdPartyAppMemberService thirdPartyAppMemberService) {
    this.thirdPartyAppMemberService = thirdPartyAppMemberService;
  }

  /// 检查用户权限
  /// @param groupId 应用ID
  /// @param userId 用户ID
  /// @param permission 权限标识
  /// @throws DataPermissionForbiddenException 权限不足时抛出异常
  public void check(String groupId, String userId, String permission) {
    thirdPartyAppMemberService
        .queryMember(groupId, userId)
        .orElseThrow(() -> new DataPermissionForbiddenException(groupId, userId, permission));
  }
}
