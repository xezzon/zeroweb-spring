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

package io.github.xezzon.zeroweb.crypto;

import io.github.xezzon.zeroweb.auth.JwtClaim;
import org.jspecify.annotations.NonNull;

/// `JwtCryptoService` 声明了与 JWT 签名/验证相关的操作。
///
/// 实现类负责生成基于 `JwtClaim` 的签名字符串，以及导出公钥等与 JWT 加密相关的操作。
///
/// @author xezzon
public interface JwtCryptoService {

  /// 根据给定的声明生成并签名一个 JWT 字符串。
  ///
  /// @param claim JWT 声明对象
  /// @return 生成的 JWT 字符串
  String signJwt(@NonNull JwtClaim claim);
}
