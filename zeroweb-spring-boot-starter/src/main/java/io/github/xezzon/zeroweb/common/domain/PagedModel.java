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

/**
 * 分页数据
 * @author xezzon
 */
@Setter(AccessLevel.PACKAGE)
@Getter
public class PagedModel<T> {

  /**
   * 数据内容
   */
  private List<T> content;
  /**
   * 分页信息
   */
  private PageMetadata page;

  /**
   * 分页信息
   */
  @Setter(AccessLevel.PACKAGE)
  @Getter
  public static class PageMetadata {

    /**
     * 分页大写
     */
    private long size;
    /**
     * 页码
     */
    private long number;
    /**
     * 总数据量
     */
    private long totalElements;
    /**
     * 总页数
     */
    private long totalPages;
  }
}
