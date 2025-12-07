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

package io.github.xezzon.zeroweb.auth.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import org.jspecify.annotations.NullMarked;

/// `InvalidPasswordException` 表示提供的密码不正确，或用户不存在。
///
/// 这是一个业务异常，用于处理认证过程中密码验证失败的情况。
///
/// @author xezzon
@NullMarked
public class InvalidPasswordException extends ZerowebBusinessException {

  /// 错误码，用于标识密码无效或用户不存在的错误。
  public static final String ERROR_CODE = "CFF01";

  /// 构造一个新的 `InvalidPasswordException` 实例。
  public InvalidPasswordException() {
    super("Password is invalid, or user is not existed.");
  }

  @Override
  public String code() {
    return ERROR_CODE;
  }
}
