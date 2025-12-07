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

package io.github.xezzon.zeroweb.openapi.enumeration;

import io.github.xezzon.zeroweb.core.trait.IDict;

/// 对外接口状态枚举
///
/// 定义了对外接口的生命周期状态，包括草稿和已发布两个状态。
/// 该枚举实现了 [IDict] 接口，
/// 提供了统一的数据字典功能。
///
/// @author xezzon
public enum OpenapiStatus implements IDict {

  /**
   * 草稿状态
   */
  DRAFT("草稿"),
  /**
   * 已发布状态
   */
  PUBLISHED("已发布"),
  ;

  private final String label;

  OpenapiStatus(String label) {
    this.label = label;
  }

  @Override
  public String getTag() {
    return OpenapiStatus.class.getSimpleName();
  }

  @Override
  public String getCode() {
    return this.name();
  }

  @Override
  public String getLabel() {
    return this.label;
  }

  @Override
  public int getOrdinal() {
    return this.ordinal();
  }
}
