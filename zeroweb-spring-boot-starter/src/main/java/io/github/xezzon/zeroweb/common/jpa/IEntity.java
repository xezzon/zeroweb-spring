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

package io.github.xezzon.zeroweb.common.jpa;

import jakarta.persistence.MappedSuperclass;

/// 基础实体接口。
///
/// 所有需要持久化的实体类都应实现此接口，以提供统一的 ID 访问方式。
///
/// @param <T> 实体 ID 的类型
/// @author xezzon
@MappedSuperclass
public interface IEntity<T> {

  /// 获取实体 ID。
  ///
  /// @return 实体 ID
  T getId();
}
