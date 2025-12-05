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

package io.github.xezzon.zeroweb.common.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/// `PagedModel` 是一个用于封装分页数据的通用模型。
/// 它包含当前页的数据内容和分页元数据。
///
/// @param <T> 数据内容的类型。
/// @author xezzon
@Setter(AccessLevel.PACKAGE)
@Getter
public class PagedModel<T> {

  /// 当前页的数据内容列表。
  ///
  /// 例如，如果查询用户列表，这里将包含当前页的用户对象列表。
  private List<T> content;
  /// 分页元数据，包含分页大小、页码、总数据量和总页数等信息。
  private PageMetadata page;

  /// `PageMetadata` 提供了关于分页的详细信息，例如总页数、当前页码等。
  @Setter(AccessLevel.PACKAGE)
  @Getter
  public static class PageMetadata {

    /// 当前页的分页大小，即每页包含的元素数量。
    ///
    /// 例如，如果 `size` 为 10，则表示每页显示 10 条数据。
    private long size;
    /// 当前页码，通常从 0 开始计数。
    ///
    /// 例如，如果 `number` 为 0，表示第一页；如果为 1，表示第二页。
    private long number;
    /// 所有页的总数据量。
    ///
    /// 例如，如果数据库中有 100 条记录，`totalElements` 将是 100。
    private long totalElements;
    /// 总页数，根据总数据量和分页大小计算得出。
    ///
    /// 例如，如果 `totalElements` 为 100 且 `size` 为 10，则 `totalPages` 将是 10。
    private long totalPages;
  }
}
