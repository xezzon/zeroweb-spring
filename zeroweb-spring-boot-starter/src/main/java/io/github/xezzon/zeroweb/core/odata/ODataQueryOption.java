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

import lombok.Builder;
import lombok.Getter;

/// OData 查询选项。
/// 封装了 OData 协议中常见的查询参数，如 `$filter`, `$orderby`, `$select`, `$top`, `$skip`, `$search`。
/// 这些选项用于对集合资源进行过滤、排序、选择、分页和搜索。
/// @see <a href="https://docs.oasis-open.org/odata/odata/v4.01/cs01/abnf/odata-abnf-construction-rules.txt">OData ABNF Construction Rules Version 4.01</a>
@SuppressWarnings("ClassCanBeRecord")
@Builder()
public class ODataQueryOption {

  /// 筛选参数
  /// @see <a href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#_Toc31360956">Common Expression Syntax</a>
  private final String filter;
  /// 排序参数
  /// @see <a href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#sec_SystemQueryOptionorderby">System Query Option $orderby</a>
  private final String orderby;
  /// 查询的列
  /// @see <a href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#sec_SystemQueryOptionselect">System Query Option $select</a>
  private final String select;
  /// 分页大小
  /// @see <a href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#sec_SystemQueryOptionstopandskip">System Query Options $top and $skip</a>
  @Getter
  private final Integer top;
  /// 分页
  /// @see <a href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#sec_SystemQueryOptionstopandskip">System Query Options $top and $skip</a>
  private final Integer skip;
  /// 搜索参数
  /// @see <a href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#_Toc31361044">System Query Option $search</a>
  private final String search;

  /// 计算当前页码。
  /// 如果 `$skip` 为 `null`，则返回 0。否则，返回 `skip / top`。
  /// @return 当前页码。
  public Integer getPageNumber() {
    if (skip == null) {
      return 0;
    }
    return this.skip / this.top;
  }
}
