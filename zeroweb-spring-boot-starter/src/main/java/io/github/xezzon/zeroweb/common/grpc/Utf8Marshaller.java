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

package io.github.xezzon.zeroweb.common.grpc;

import io.grpc.Metadata.BinaryMarshaller;
import java.nio.charset.StandardCharsets;

/// `Utf8Marshaller` 用于在 gRPC `Metadata` 中序列化和反序列化 UTF-8 编码的字符串。
/// 它实现了 `BinaryMarshaller<String>` 接口，提供了将字符串转换为字节数组和将字节数组解析回字符串的方法。
///
/// @author xezzon
public class Utf8Marshaller implements BinaryMarshaller<String> {

  /// 将字符串序列化为 UTF-8 字节数组。
  /// 如果输入字符串为 `null`，则返回一个空字节数组。
  ///
  /// @param s 要序列化的字符串。
  /// @return 表示输入字符串的 UTF-8 字节数组。
  @Override
  public byte[] toBytes(final String s) {
    if (s == null) {
      return new byte[0];
    }
    return s.getBytes(StandardCharsets.UTF_8);
  }

  /// 从 UTF-8 字节数组中反序列化字符串。
  /// 如果输入字节数组为 `null` 或为空，则返回一个空字符串。
  ///
  /// @param bytes 要反序列化的字节数组。
  /// @return 从输入字节数组解析的字符串。
  @Override
  public String parseBytes(final byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      return "";
    }
    return new String(bytes, StandardCharsets.UTF_8);
  }
}
