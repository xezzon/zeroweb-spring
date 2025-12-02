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

/**
 * @author xezzon
 */
public class ErrorCodeConstant {

  public static final int CLIENT_ERROR_STATUS = HttpResponseStatus.UNPROCESSABLE_ENTITY.code();
  public static final int SERVER_ERROR_STATUS = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
  public static final String UNKNOWN = "S0001";
  public static final String UNAUTHENTICATED = "C0002";
  public static final String UNAUTHORIZED = "C0003";
  public static final String ARGUMENT_INVALID = "C0005";
  public static final String NO_SUCH_DATA = "C0008";
  /**
   * 错误码的请求头名称
   */
  public static final String ERROR_CODE_HEADER = "X-Error-Code";

  private ErrorCodeConstant() {
  }
}
