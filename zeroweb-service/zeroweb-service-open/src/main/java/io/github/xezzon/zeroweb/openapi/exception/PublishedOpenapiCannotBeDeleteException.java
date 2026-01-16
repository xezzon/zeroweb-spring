/*
 * SPDX-FileCopyrightText: Copyright (C) 2025-2026 xezzon
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

package io.github.xezzon.zeroweb.openapi.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import org.jspecify.annotations.NullMarked;

/// 已发布的对外接口无法删除异常
///
/// 当尝试删除状态为已发布的对外接口时，抛出此业务异常。
///
/// @author xezzon
@NullMarked
public class PublishedOpenapiCannotBeDeleteException extends ZerowebBusinessException {

  /// 错误码
  public static final String ERROR_CODE = "CFE06";

  /// 默认构造函数
  public PublishedOpenapiCannotBeDeleteException() {
    super("Published OpenAPI cannot be deleted.");
  }

  @Override
  public String code() {
    return ERROR_CODE;
  }
}
