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

/// 无效的访问密钥
///
/// 原因：
/// 1. 没传AccessKey或摘要
/// 2. AccessKey或签名使用的SecretKey不正确
/// 3. AccessKey或签名使用的SecretKey不匹配
///
/// @author xezzon
@NullMarked
public class InvalidAccessKeyException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "CFE03";

  public InvalidAccessKeyException() {
    super("An Invalid Access Key");
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }

  @Override
  public int getHttpStatus() {
    return HttpResponseStatus.FORBIDDEN.code();
  }
}
