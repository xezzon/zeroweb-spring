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

/// 提供了从各种格式读取 ASN.1 编码私钥的功能。
/// 该接口定义了读取私钥的方法，具体实现由实现类提供。
/// @author xezzon
public interface ASN1PrivateKeyReader {

  /// 从底层源读取并解析 ASN.1 编码的私钥。
  /// 此方法旨在处理私钥的通用读取逻辑，并将其转换为一个通用的 Java 对象。
  /// @return 表示 ASN.1 编码私钥的通用对象。
  /// @throws IOException 如果在读取或解析私钥过程中发生 I/O 错误。
  Object readPrivateKey() throws IOException;
}
