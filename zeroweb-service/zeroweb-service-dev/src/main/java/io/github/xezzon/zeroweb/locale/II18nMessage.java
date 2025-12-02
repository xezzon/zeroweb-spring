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

package io.github.xezzon.zeroweb.locale;

import jakarta.persistence.MappedSuperclass;

/// 需要国际化的功能，需要实现该接口
@MappedSuperclass
public interface II18nMessage {

  /// @return 命名空间
  String getNamespace();

  /// @return 国际化内容
  String getMessageKey();

  /// 当命名空间与键都相同时，认为是同一国际化内容
  ///
  /// @param that 另一个实现了国际化内容接口的对象
  /// @return 是否认定同一
  default boolean eq(final II18nMessage that) {
    return getNamespace().equals(that.getNamespace())
        && getMessageKey().equals(that.getMessageKey());
  }
}
