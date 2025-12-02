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
 * 定义调用对外接口的常量
 * @author xezzon
 */
public class ZerowebOpenConstant {

  /**
   * 摘要算法
   */
  public static final String DIGEST_ALGORITHM = "HmacSHA256";
  /**
   * 应用访问凭据的请求头
   */
  public static final String ACCESS_KEY_HEADER = "X-Access-Key";
  /**
   * 摘要生成时间戳的请求头
   */
  public static final String TIMESTAMP_HEADER = "X-Timestamp";
  /**
   * 摘要的请求头
   */
  public static final String SIGNATURE_HEADER = "X-Signature";

  private ZerowebOpenConstant() {
  }
}
