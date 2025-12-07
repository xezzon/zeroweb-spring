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

package io.github.xezzon.zeroweb.metadata;

import io.github.xezzon.zeroweb.core.trait.IDict;

/// 定义服务的类型，例如前端或后端服务。
/// 实现 [IDict] 接口，支持字典化。
///
/// @author xezzon
public enum ServiceType implements IDict {
  /// 前端
  CLIENT("前端"),
  /// 后端
  SERVER("后端"),
  ;

  /// 服务类型的显示标签。
  private final String label;

  /// 构造函数，初始化服务类型及其标签。
  ///
  /// @param label 服务类型的可读标签。
  ServiceType(final String label) {
    this.label = label;
  }

  /// 获取服务类型对应的标签（字典类型名称）。
  ///
  /// @return 字典类型名称，即 "ServiceType"。
  @Override
  public String getTag() {
    return ServiceType.class.getSimpleName();
  }

  /// 获取服务类型的代码表示。
  ///
  /// @return 服务类型的枚举名称字符串。
  @Override
  public String getCode() {
    return this.name();
  }

  /// 获取服务类型的可读标签。
  ///
  /// @return 服务类型的显示名称。
  @Override
  public String getLabel() {
    return this.label;
  }

  /// 获取服务类型在枚举中的序数。
  ///
  /// @return 服务类型在枚举定义中的位置（从0开始）。
  @Override
  public int getOrdinal() {
    return this.ordinal();
  }
}
