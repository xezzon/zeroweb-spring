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
 * ZeroWeb 开放接口异常。
 * 在调用 ZeroWeb 开放接口时，如果发生任何底层异常，都会被封装成此运行时异常。
 * 这有助于统一异常处理逻辑，提高代码的可读性和健壮性。
 */
public class ZerowebOpenException extends RuntimeException {

  /**
   * 使用指定的根本原因构造一个新的 ZeroWeb 开放接口异常。
   * @param cause 异常的根本原因。
   */
  public ZerowebOpenException(Throwable cause) {
    super(cause);
  }
}
