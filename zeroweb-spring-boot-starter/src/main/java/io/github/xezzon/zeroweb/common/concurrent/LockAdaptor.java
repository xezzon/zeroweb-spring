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

package io.github.xezzon.zeroweb.common.concurrent;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author xezzon
 */
public interface LockAdaptor {

  /// 加锁执行操作
  /// @param id 资源ID
  /// @param supplier 要执行的操作
  /// @return 操作返回值，如果获取锁失败则返回 empty
  /// @param <R> 操作返回类型
  <R> Optional<R> tryLock(String id, Supplier<R> supplier);
}
