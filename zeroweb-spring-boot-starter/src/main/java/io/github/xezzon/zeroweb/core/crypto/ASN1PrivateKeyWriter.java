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

import java.io.IOException;

/// 提供将ASN.1格式的私钥写入输出流的功能。
///
/// @author xezzon
public interface ASN1PrivateKeyWriter {

  /// 将ASN.1形式的私钥写入。
  ///
  /// @param privateKey 私钥的字节数组
  /// @throws IOException 如果写入操作失败，例如由于I/O错误
  void writePrivateKey(byte[] privateKey) throws IOException;
}
