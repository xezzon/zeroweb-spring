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

import java.util.Collections;
import java.util.Map;
import lombok.Getter;

/// 抽象的业务异常基类。
/// 所有业务相关的异常都应该继承此抽象类。
/// @author xezzon
public abstract class ZerowebBusinessException extends RuntimeException {

  /// 异常附带的参数，通常用于国际化消息的占位符替换。
  @Getter
  private final transient Map<String, Object> parameters;

  /// 使用指定的详细消息构造一个 `ZerowebBusinessException`。
  /// @param message 详细消息
  protected ZerowebBusinessException(String message) {
    super(message);
    this.parameters = Collections.emptyMap();
  }

  /// 使用指定的参数和详细消息构造一个 `ZerowebBusinessException`。
  /// @param parameters 异常附带的参数
  /// @param message 详细消息
  protected ZerowebBusinessException(
      final Map<String, Object> parameters,
      final String message
  ) {
    super(message);
    this.parameters = parameters;
  }

  /// 业务异常的错误码。
  /// @return 错误码字符串
  public abstract String code();

  /// 业务异常对应的 HTTP 状态码。
  /// @return HTTP 状态码
  public int httpStatus() {
    return ErrorCodeConstant.CLIENT_ERROR_STATUS;
  }
}
