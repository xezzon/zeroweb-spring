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

package io.github.xezzon.zeroweb.subscription;

import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

/// @author xezzon
public interface ISubscriptionService4ThirdPartyApp {

  /// 获取订阅列表
  /// @param odata OData查询选项，用于指定查询条件、排序方式等。
  /// @param appId 应用程序ID。
  /// @return 包含订阅信息的分页对象。
  /// @throws DataPermissionForbiddenException 只有应用所有者有权限访问
  Page<@NonNull Subscription> listSubscription(ODataQueryOption odata, String appId)
      throws DataPermissionForbiddenException;
}
