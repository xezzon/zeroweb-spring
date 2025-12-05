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

package io.github.xezzon.zeroweb.common.exception;

import java.io.Serial;

/// 当用户提供的身份验证令牌无效时抛出此异常。
/// 这通常发生在令牌过期、被篡改或格式不正确时。
/// @author xezzon
public class InvalidTokenException extends ZerowebRuntimeException {

  @Serial
  private static final long serialVersionUID = 4676151668260963197L;

  /**
   * 使用指定的原因构造一个新的 `InvalidTokenException`。
   *
   * @param e 导致此异常的原因。
   */
  public InvalidTokenException(Throwable e) {
    super(e);
  }
}
