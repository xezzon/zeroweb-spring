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

/// 用于定义将 ASN.1 编码的公钥写入的契约。
///
/// @author xezzon
public interface ASN1PublicKeyWriter {

  /// 将 ASN.1 形式的公钥写入。
  ///
  /// @param publicKey ASN.1 编码的公钥字节数组。
  /// @throws IOException 如果在写入过程中发生 I/O 错误。
  void writePublicKey(byte[] publicKey) throws IOException;
}
