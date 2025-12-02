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

package io.github.xezzon.zeroweb.auth;

/**
 * HTTP 认证相关的常量
 * @author xezzon
 */
public final class AuthHttpConstant {

  private AuthHttpConstant() {
  }

  /**
   * HTTP "Authentication" 请求头
   * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Reference/Headers/Authorization">Authorization - HTTP | MDN</a>
   */
  public static final String AUTHORIZATION = "Authorization";

  /**
   * HTTP "Bearer" 身份验证方案
   * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Guides/Authentication#bearer">HTTP 身份验证 - HTTP | MDN</a>
   */
  public static final String BEARER = "Bearer";
}
