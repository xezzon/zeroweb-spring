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

package io.github.xezzon.zeroweb;

/**
 * 定义调用 Zeroweb 对外开放接口时使用的常量。
 * 包含请求头名称和摘要算法等信息，用于构建和验证签名。
 */
public class ZerowebOpenConstant {

  /**
   * 用于签名生成的摘要算法名称。
   * 当前使用 `HmacSHA256` 算法，确保请求的完整性和认证性。
   */
  public static final String DIGEST_ALGORITHM = "HmacSHA256";
  /**
   * 请求头，用于携带应用访问凭据（Access Key）。
   * 在每次调用开放接口时，客户端需将 Access Key 放入此请求头进行身份验证。
   */
  public static final String ACCESS_KEY_HEADER = "X-Access-Key";
  /**
   * 请求头，用于携带摘要生成时的时间戳。
   * 时间戳用于防止重放攻击，确保请求在有效时间内被处理。
   */
  public static final String TIMESTAMP_HEADER = "X-Timestamp";
  /**
   * 请求头，用于携带请求的签名。
   * 签名是对请求内容进行摘要计算的结果，用于验证请求的完整性和真实性。
   */
  public static final String SIGNATURE_HEADER = "X-Signature";

  private ZerowebOpenConstant() {
  }
}
