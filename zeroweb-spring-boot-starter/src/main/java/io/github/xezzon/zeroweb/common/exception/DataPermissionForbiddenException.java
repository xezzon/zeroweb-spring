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

package io.github.xezzon.zeroweb.common.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Map;

/// 数据权限不足异常。
///
/// 错误码：`C0007`
///
/// @author xezzon
public class DataPermissionForbiddenException extends ZerowebBusinessException {

  /// 错误码
  public static final String ERROR_CODE = "C0007";

  /// 构造器。
  ///
  /// @param groupId 组ID
  /// @param userId 用户ID
  /// @param permission 权限
  public DataPermissionForbiddenException(String groupId, String userId, String permission) {
    super(
        Map.ofEntries(
            Map.entry("groupId", groupId),
            Map.entry("userId", userId),
            Map.entry("permission", permission)
        ),
        String.format("`%s` has no permission `%s` for `%s`.", userId, permission, groupId)
    );
  }

  @Override
  public String code() {
    return ERROR_CODE;
  }

  /// 该错误返回 403 HTTP 状态码
  ///
  /// @return HTTP状态码
  @Override
  public int httpStatus() {
    return HttpResponseStatus.FORBIDDEN.code();
  }
}
