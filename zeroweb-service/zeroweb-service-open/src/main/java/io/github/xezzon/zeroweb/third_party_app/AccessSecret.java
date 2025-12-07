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

package io.github.xezzon.zeroweb.third_party_app;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Getter;
import lombok.Setter;

/// 第三方应用访问凭据与密钥
/// @author xezzon
@Getter
@Setter
@Entity
@Table(name = ThirdPartyApp.TABLE_NAME)
public class AccessSecret {

  /// 密钥列名
  public static final String SECRET_KEY_COLUMN = "secret_key";

  /// 第三方应用标识
  @Id
  @Column(
      name = DatabaseConstant.ID_COLUMN,
      nullable = false,
      insertable = false,
      updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  String id;
  /// 第三方应用密钥，用于签名验证
  @Column(name = SECRET_KEY_COLUMN, nullable = false, length = 64)
  String secretKey;

  /// @return 第三方应用访问凭据
  public String getAccessKey() {
    byte[] accessKey = this.id.getBytes(StandardCharsets.UTF_8);
    return Base64.getEncoder()
        .encodeToString(accessKey);
  }
}
