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

/// ZeroWeb 自发抛出的运行时异常基类。
/// 所有 ZeroWeb 内部业务逻辑或系统操作中抛出的运行时异常都应继承此类。
///
/// 提供了两个构造函数：
/// 1. 接受详细消息和根本原因。
/// 2. 仅接受根本原因。
///
/// @author xezzon
public class ZerowebRuntimeException extends RuntimeException {

  /// 构造一个新的 `ZerowebRuntimeException`，附带指定的详细消息和根本原因。
  ///
  /// @param message 异常的详细消息。
  /// @param cause 根本原因（稍后可通过 `Throwable.getCause()` 方法检索）。
  public ZerowebRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /// 构造一个新的 `ZerowebRuntimeException`，附带指定的根本原因和详细消息 `(cause==null ? null : cause.toString())`（通常包含 `cause` 的类和详细消息）。
  /// 此构造函数对于包装其他可抛出对象以进行重新抛出非常有用。
  ///
  /// @param cause 根本原因（稍后可通过 `Throwable.getCause()` 方法检索）。
  ///              `null` 值表示原因不存在或未知。
  public ZerowebRuntimeException(Throwable cause) {
    super(cause);
  }
}
