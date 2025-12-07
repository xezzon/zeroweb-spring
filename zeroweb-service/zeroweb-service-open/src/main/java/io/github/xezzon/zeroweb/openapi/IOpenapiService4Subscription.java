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

package io.github.xezzon.zeroweb.openapi;

import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;

/// 对外接口订阅服务接口
///
/// 为订阅服务提供查询对外接口的能力，包括根据编码查询接口和分页查询已发布接口列表。
/// 该接口仅对外暴露查询相关的方法，不包含修改操作，
/// 确保订阅服务的接口只读特性。
///
/// @author xezzon
public interface IOpenapiService4Subscription {

  /// 根据编码查询对外接口
  ///
  /// @param openapiCode 接口编码
  /// @return 对外接口编码
  @Nullable Openapi getByCode(String openapiCode);

  /// 查询符合条件的OpenAPI列表
  ///
  /// @param odata OData查询参数，用于指定查询条件、排序方式、分页信息等
  /// @return 符合查询条件的OpenAPI分页结果
  Page<@NonNull Openapi> listPublishedOpenapi(ODataQueryOption odata);
}
