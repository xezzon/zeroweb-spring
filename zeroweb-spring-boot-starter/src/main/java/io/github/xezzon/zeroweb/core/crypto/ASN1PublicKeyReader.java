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

/// `ASN1PublicKeyReader` 接口定义了从 ASN.1 格式读取公钥的方法。
/// 实现此接口的类负责解析 ASN.1 编码的公钥数据。
///
/// @author xezzon
public interface ASN1PublicKeyReader {

  /// 读取公钥形式的 ASN.1 数据。
  ///
  /// @return 表示公钥的 ASN.1 对象。
  /// @throws IOException 如果在读取或解析公钥时发生 I/O 错误。
  Object readPublicKey() throws IOException;
}
