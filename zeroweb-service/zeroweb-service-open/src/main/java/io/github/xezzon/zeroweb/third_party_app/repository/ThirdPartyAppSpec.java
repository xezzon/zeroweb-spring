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

package io.github.xezzon.zeroweb.third_party_app.repository;

import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp_;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/// 第三方应用查询规范
///
/// 定义第三方应用的查询条件和排序规则
///
/// @author xezzon
public class ThirdPartyAppSpec {

  /// 私有构造函数，防止实例化
  private ThirdPartyAppSpec() {
  }

  /// 获取默认排序规则
  ///
  /// 按创建时间降序排序
  ///
  /// @return 排序对象
  public static Sort defaultSort() {
    return Sort.by(Order.desc(ThirdPartyApp_.CREATE_TIME));
  }
}
