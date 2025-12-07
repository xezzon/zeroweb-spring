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

package io.github.xezzon.zeroweb.core.crypto;

import java.util.Base64;

/// 从 Base64 编码的 DER 字符串中读取 ASN.1 公钥。
/// 该类实现了 [ASN1PublicKeyReader] 接口，提供了解码 Base64 字符串并返回原始字节数组的功能。
///
/// @author xezzon
public class DerStringReader implements ASN1PublicKeyReader {

  /// 用于 Base64 解码的解码器实例。
  private final Base64.Decoder decoder = Base64.getDecoder();
  /// 存储待解码的 Base64 编码的 DER 字符串。
  private final String derBase64;

  /// 构造一个新的 `DerStringReader` 实例。
  ///
  /// @param derBase64 Base64 编码的 DER 字符串。
  public DerStringReader(String derBase64) {
    this.derBase64 = derBase64;
  }

  /// 解码存储的 Base64 字符串，并以字节数组的形式返回原始的 DER 数据。
  ///
  /// @return 包含解码后的 DER 数据的字节数组。
  @Override
  public Object readPublicKey() {
    return decoder.decode(derBase64);
  }
}
