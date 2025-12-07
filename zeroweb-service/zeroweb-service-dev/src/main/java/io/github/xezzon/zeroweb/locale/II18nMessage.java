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

/// 国际化消息接口。
///
/// 任何需要支持国际化功能的对象都应实现此接口，
/// 提供其命名空间和消息键，以便进行国际化文本的查找和匹配。
@MappedSuperclass
public interface II18nMessage {

/// 获取国际化内容的命名空间。
///
/// 命名空间用于逻辑分组国际化消息。
///
/// @return 国际化消息的命名空间字符串。
  String getNamespace();

/// 获取国际化消息的键。
///
/// 消息键在特定命名空间内唯一标识一个国际化消息。
///
/// @return 国际化消息的键字符串。
  String getMessageKey();

/// 判断当前国际化消息与另一个 [II18nMessage] 对象是否相同。
///
/// 当两个对象的命名空间和消息键都一致时，认为它们代表同一个国际化内容。
///
/// @param that 另一个实现了 [II18nMessage] 接口的对象。
/// @return 如果命名空间和消息键都相同则返回 `true`，否则返回 `false`。
  default boolean eq(final II18nMessage that) {
    return getNamespace().equals(that.getNamespace())
        && getMessageKey().equals(that.getMessageKey());
  }
}
