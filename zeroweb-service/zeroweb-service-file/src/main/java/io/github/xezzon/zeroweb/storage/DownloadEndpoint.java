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

package io.github.xezzon.zeroweb.storage;

import lombok.Getter;

/// 附件下载地址
/// @author xezzon
@Getter
public class DownloadEndpoint {

  /// 下载地址
  private String endpoint;

  /**
   * 默认构造函数
   */
  @SuppressWarnings("unused")
  DownloadEndpoint() {
    super();
  }

  /**
   * 构造附件下载地址
   * @param endpoint 下载地址URL
   */
  public DownloadEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }
}
