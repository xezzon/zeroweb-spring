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

import org.jspecify.annotations.NullMarked;

/// 上传文件的内容与创建的附件不一致
/// @author xezzon
@NullMarked
public class IncorrectFileException extends ZerowebBusinessException {

  /// 错误码
  public static final String ERROR_CODE = "CFC02";

  /// 创建一个 [IncorrectFileException] 实例。
  /// @param message 错误信息
  public IncorrectFileException(String message) {
    super(message);
  }

  @Override
  public String code() {
    return ERROR_CODE;
  }
}
