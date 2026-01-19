/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 xezzon
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

package io.github.xezzon.zeroweb.core.trait;

/**
 * 将源对象与目标对象合并
 * @author xezzon
 * @param <S> 源对象类型
 * @param <T> 目标对象类型
 */
public interface Merge<S, T> {

  /// 将源对象与目标对象合并
  /// 如果字段在源对象中存在，则合并后的对象，该字段的值取自源对象；否则取自目标对象。
  /// @param value 源对象
  /// @param origin 目标对象
  /// @return 合并后的对象
  T merge(S value, T origin);
}
