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

package io.github.xezzon.zeroweb.auth.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.github.xezzon.zeroweb.auth.AuthHttpConstant;
import lombok.Getter;
import lombok.Setter;

/// `OidcToken` 表示遵循 OpenID Connect (OIDC) 规范的 Token 响应体。
///
/// 包含访问令牌、刷新令牌、ID 令牌以及它们的有效期等信息。
///
/// @see <a href="https://openid.net/specs/openid-connect-core-1_0.html">OIDC 规范</a>
/// @author xezzon
@Getter
@Setter
@JsonNaming(SnakeCaseStrategy.class)
public class OidcToken {

  /// 访问令牌，用于访问受保护的资源。
  private String accessToken;
  /// 刷新令牌，用于获取新的访问令牌。
  private String refreshToken;
  /// 过期时间
  ///
  /// 单位：秒
  private Long expiresIn;
  /// ID 令牌，包含用户的身份信息。
  private String idToken;

  /// 构造一个新的 `OidcToken` 实例。
  ///
  /// @param accessToken 访问令牌。
  /// @param idToken ID 令牌。
  /// @param expiresIn 令牌的过期时间（秒）。
  public OidcToken(String accessToken, String idToken, Long expiresIn) {
    this.accessToken = accessToken;
    this.idToken = idToken;
    this.expiresIn = expiresIn;
  }

  /// 获取令牌类型，默认为 "Bearer"。
  ///
  /// @return 令牌类型字符串。
  @SuppressWarnings("unused")
  public String getTokenType() {
    return AuthHttpConstant.BEARER;
  }
}
