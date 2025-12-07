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

package io.github.xezzon.zeroweb.subscription.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jspecify.annotations.NullMarked;

/// 不能调用未订阅的接口异常，当尝试调用未订阅的对外接口时抛出
///
/// @author xezzon
@NullMarked
public class UnsubscribeOpenapiException extends ZerowebBusinessException {

  /// 错误码：不能调用未订阅的接口
  public static final String ERROR_CODE = "CFE04";

  /// 构造器，设置默认错误信息
  public UnsubscribeOpenapiException() {
    super("Cannot call an unsubscribed OpenAPI.");
  }

  @Override
  public String code() {
    return ERROR_CODE;
  }

  /// @return HTTP状态码（403 Forbidden）
  @Override
  public int httpStatus() {
    return HttpResponseStatus.FORBIDDEN.code();
  }
}
