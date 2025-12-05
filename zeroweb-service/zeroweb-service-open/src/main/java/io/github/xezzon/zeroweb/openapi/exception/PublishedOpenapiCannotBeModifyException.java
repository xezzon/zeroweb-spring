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

package io.github.xezzon.zeroweb.openapi.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import org.jspecify.annotations.NullMarked;

/// 已发布的对外接口无法修改异常
///
/// 当尝试修改状态为已发布的对外接口时，抛出此业务异常。
/// 根据业务规则，已发布的接口编码和基本信息不允许修改，
/// 只能修改草稿状态的接口信息。
///
/// @author xezzon
@NullMarked
public class PublishedOpenapiCannotBeModifyException extends ZerowebBusinessException {

  /// 错误码
  public static final String ERROR_CODE = "CFE01";

  /// 默认构造函数
  public PublishedOpenapiCannotBeModifyException() {
    super("Published OpenAPI cannot be modified.");
  }

  @Override
  public String code() {
    return ERROR_CODE;
  }
}
