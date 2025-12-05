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

import io.netty.handler.codec.http.HttpResponseStatus;

/// 错误码常量定义。
///
/// 包含客户端错误、服务端错误状态码以及各种具体的业务错误码。
///
/// @author xezzon
public class ErrorCodeConstant {

  /// 客户端错误 HTTP 状态码，通常为 422 Unprocessable Entity。
  public static final int CLIENT_ERROR_STATUS = HttpResponseStatus.UNPROCESSABLE_ENTITY.code();
  /// 服务端错误 HTTP 状态码，通常为 500 Internal Server Error。
  public static final int SERVER_ERROR_STATUS = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
  /// 未知错误。
  public static final String UNKNOWN = "S0001";
  /// 未认证错误（通常指用户未登录或 Token 无效）。
  public static final String UNAUTHENTICATED = "C0002";
  /// 未授权错误（通常指用户无权访问某个资源或执行某个操作）。
  public static final String UNAUTHORIZED = "C0003";
  /// 请求参数无效错误。
  public static final String ARGUMENT_INVALID = "C0005";
  /// 数据不存在错误。
  public static final String NO_SUCH_DATA = "C0008";
  /// 错误码的请求头名称。
  public static final String ERROR_CODE_HEADER = "X-Error-Code";

  /// 私有构造函数，防止实例化。
  private ErrorCodeConstant() {
  }
}
