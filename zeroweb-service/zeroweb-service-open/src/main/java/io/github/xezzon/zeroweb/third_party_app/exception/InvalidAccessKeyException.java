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

package io.github.xezzon.zeroweb.third_party_app.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jspecify.annotations.NullMarked;

/// 无效的访问密钥异常
///
/// 当第三方应用访问密钥验证失败时抛出此异常。可能的原因包括：
/// 1. 未传递AccessKey或消息摘要
/// 2. AccessKey或签名使用的SecretKey不正确
/// 3. AccessKey或签名使用的SecretKey不匹配
/// 4. 访问密钥已过期或被吊销
///
/// @author xezzon
@NullMarked
public class InvalidAccessKeyException extends ZerowebBusinessException {

  /// 错误代码
  public static final String ERROR_CODE = "CFE03";

  /// 默认构造函数
  public InvalidAccessKeyException() {
    super("An Invalid Access Key");
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
