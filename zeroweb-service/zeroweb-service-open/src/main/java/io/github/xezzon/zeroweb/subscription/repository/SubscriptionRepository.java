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

package io.github.xezzon.zeroweb.subscription.repository;

import io.github.xezzon.zeroweb.subscription.Subscription;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// 订阅数据访问接口，提供对订阅数据的CRUD操作和查询功能
/// @author xezzon
@Repository
@NullMarked
public interface SubscriptionRepository extends
    JpaRepository<Subscription, String>,
    JpaSpecificationExecutor<Subscription> {

  /// 根据应用ID和接口编码集合查询订阅记录
  /// @param appId 应用ID
  /// @param openapiCodes 接口编码集合
  /// @return 订阅记录列表
  List<Subscription> findByAppIdAndOpenapiCodeIn(String appId, Collection<String> openapiCodes);

  /// 根据应用ID查询所有订阅记录
  /// @param appId 应用ID
  /// @return 订阅记录列表
  List<Subscription> findByAppId(String appId);
}
