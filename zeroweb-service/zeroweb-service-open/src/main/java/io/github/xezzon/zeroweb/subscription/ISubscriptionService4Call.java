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

import io.github.xezzon.zeroweb.subscription.exception.UnsubscribeOpenapiException;

/// 订阅服务向调用服务暴露的接口
/// @author xezzon
public interface ISubscriptionService4Call {

  /// 获取指定应用ID下被订阅的对外接口
  ///
  /// @param appId 应用ID
  /// @param openapiCode 对外接口编码
  /// @return 对外接口详情
  /// @throws UnsubscribeOpenapiException 不能调用未订阅的对外接口
  Subscription getSubscription(String appId, String openapiCode);
}
