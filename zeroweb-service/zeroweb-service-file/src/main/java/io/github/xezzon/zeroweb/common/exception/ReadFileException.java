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

package io.github.xezzon.zeroweb.common.exception;

/// 文件读取错误。
///
/// 文件服务在下载文件时发生的任何错误，都应该抛出此异常。
/// @author xezzon
public class ReadFileException extends ZerowebRuntimeException {

  /// 根据一个已有的异常构造一个新的文件读取异常。
  /// @param cause 原始异常
  public ReadFileException(Throwable cause) {
    super(cause);
  }
}
