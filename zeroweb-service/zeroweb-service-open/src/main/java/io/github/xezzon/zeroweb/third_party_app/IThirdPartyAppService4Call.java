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

import io.github.xezzon.zeroweb.third_party_app.exception.InvalidAccessKeyException;
import java.time.Instant;

public interface IThirdPartyAppService4Call {

  /// 校验摘要，如果校验成功则签发JWT，否则抛出异常。
  ///
  /// @param accessKey AccessKey
  /// @param body 消息内容
  /// @param signature 消息摘要
  /// @param iat 消息签发时间
  /// @return 携带认证信息的JWT
  /// @throws InvalidAccessKeyException 如果摘要校验失败则抛出此异常
  String signJwt(String accessKey, byte[] body, String signature, Instant iat)
      throws InvalidAccessKeyException;
}
