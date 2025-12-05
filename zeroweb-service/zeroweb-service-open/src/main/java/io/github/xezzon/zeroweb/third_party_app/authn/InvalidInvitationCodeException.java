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

package io.github.xezzon.zeroweb.third_party_app.authn;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import io.jsonwebtoken.JwtException;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jspecify.annotations.NullMarked;

/// 邀请码已过期，或者不允许被当前用户使用
///
/// @author xezzon
@NullMarked
public class InvalidInvitationCodeException extends ZerowebBusinessException {

  /// 错误码
  public static final String ERROR_CODE = "CFE05";

  /// 邀请码中指定的用户不是当前用户
  public InvalidInvitationCodeException() {
    super("This invitation code is invalid for you.");
  }

  /// 邀请码已过期
  /// @param e JWT 已过期
  public InvalidInvitationCodeException(JwtException e) {
    super("This is an invalid or expired invitation code.");
    this.initCause(e);
  }

  @Override
  public String code() {
    return ERROR_CODE;
  }

  /// @return 403 错误
  @Override
  public int httpStatus() {
    return HttpResponseStatus.FORBIDDEN.code();
  }
}
