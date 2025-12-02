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

/// @author xezzon
@Getter
public class UploadEndpoint {

  /// 分段序号
  private int partNumber;
  /// 上传地址
  private String endpoint;

  @SuppressWarnings("unused")
  public UploadEndpoint() {
    super();
  }

  /// 单文件上传的地址
  /// @param endpoint 上传地址
  public UploadEndpoint(String endpoint) {
    this.partNumber = 0;
    this.endpoint = endpoint;
  }

  /// 已经上传过的分段
  /// @param partNumber 分段序号
  public UploadEndpoint(int partNumber) {
    this.partNumber = partNumber;
  }

  /// 无需回调的文件上传地址
  /// @param partNumber 分段序号
  /// @param endpoint 上传地址
  public UploadEndpoint(int partNumber, String endpoint) {
    this.partNumber = partNumber;
    this.endpoint = endpoint;
  }
}
