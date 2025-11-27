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

/// 遵循OIDC规范的Token响应体
///
/// @see <a href="https://openid.net/specs/openid-connect-core-1_0.html">OIDC规范</a>
/// @author xezzon
@Getter
@Setter
@JsonNaming(SnakeCaseStrategy.class)
public class OidcToken {

  private String accessToken;
  private String refreshToken;
  /// 过期时间 单位：秒
  private Long expiresIn;
  private String idToken;

  public OidcToken(String accessToken, String idToken, Long expiresIn) {
    this.accessToken = accessToken;
    this.idToken = idToken;
    this.expiresIn = expiresIn;
  }

  @SuppressWarnings("unused")
  public String getTokenType() {
    return AuthHttpConstant.BEARER;
  }
}
