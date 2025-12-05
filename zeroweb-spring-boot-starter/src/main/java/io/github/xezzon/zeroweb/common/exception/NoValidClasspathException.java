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

import lombok.extern.slf4j.Slf4j;

/**
 * `NoValidClasspathException` 表示在应用程序启动或运行时未能找到任何有效的类路径。
 * 当应用程序依赖于类路径中的特定资源或配置，但这些资源无法被正确加载时，会抛出此异常。
 * 例如，当密钥文件未在预期位置找到时，可能会抛出此异常。
 *
 * @author xezzon
 * @see ZerowebRuntimeException
 */
@Slf4j
public class NoValidClasspathException extends ZerowebRuntimeException {

  /**
   * 使用指定的根本原因构造一个新的 `NoValidClasspathException`。
   *
   * @param cause 根本原因（通常是另一个异常，它导致此异常被抛出）。
   */
  public NoValidClasspathException(Throwable cause) {
    super("No valid classpath found", cause);
  }
}
