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

package io.github.xezzon.zeroweb.role.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import org.jspecify.annotations.NullMarked;

/// 角色不可继承异常
///
/// 当尝试向不可继承的角色添加下级角色时抛出此异常。
/// 该异常表明操作违反了角色的继承规则，
/// 通常是因为目标角色的 `inheritable` 字段为 `false`。
///
/// @author xezzon
@NullMarked
public class RoleNotInheritableException extends ZerowebBusinessException {

  /// 错误代码
  public static final String ERROR_CODE = "CFF02";

  /// 构造不可继承异常实例
  ///
  /// 使用默认错误消息初始化异常实例。
  /// 错误消息表明尝试向不可继承的角色添加子角色。
  public RoleNotInheritableException() {
    super("Add a role to a not inheritable one.");
  }

  @Override
  public String code() {
    return ERROR_CODE;
  }
}
