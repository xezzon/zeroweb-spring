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

package io.github.xezzon.zeroweb.core.odata;

import io.github.xezzon.zeroweb.core.trait.Into;

/// OData 请求参数
/// @param top 最大返回条数
/// @param skip 偏移量
/// @author xezzon
public record ODataRequestParam(
    Integer top,
    Integer skip
) implements Into<ODataQueryOption> {

  /// 将当前的 OData 请求参数转换为 OData 查询选项。
  /// 主要用于将贫血模型 `ODataRequestParam` 转换为充血模型 `ODataQueryOption`。
  /// @return 转换后的 OData 查询选项。
  @Override
  public ODataQueryOption into() {
    return ODataQueryOption.builder()
        .top(this.top)
        .skip(this.skip)
        .build();
  }
}
