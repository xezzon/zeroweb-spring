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

package io.github.xezzon.zeroweb.subscription.enumeration;

import io.github.xezzon.zeroweb.core.trait.IDict;

/// 订阅状态枚举，定义了接口订阅的各种状态
/// @author xezzon
public enum SubscriptionStatus implements IDict {

  /// 未订阅状态
  NONE("未订阅"),
  /// 审核中状态
  AUDITING("审核中"),
  /// 已订阅状态
  SUBSCRIBED("已订阅"),
  ;

  /// 状态标签，用于显示
  private final String label;

  /// 构造器，设置状态标签
  /// @param label 状态标签
  SubscriptionStatus(String label) {
    this.label = label;
  }

  /// 获取字典标签
  /// @return 字典标签
  @Override
  public String getTag() {
    return "OpenapiSubscription";
  }

  /// 获取字典编码
  /// @return 字典编码
  @Override
  public String getCode() {
    return this.name();
  }

  /// 获取字典标签
  /// @return 字典标签
  @Override
  public String getLabel() {
    return this.label;
  }

  /// 获取枚举序号
  /// @return 枚举序号
  @Override
  public int getOrdinal() {
    return this.ordinal();
  }
}
