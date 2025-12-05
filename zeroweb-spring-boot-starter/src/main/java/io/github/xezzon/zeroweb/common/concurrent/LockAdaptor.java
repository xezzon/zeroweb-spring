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

/// 锁适配器接口。
/// 用于提供分布式锁或本地锁的抽象。
/// @author xezzon
public interface LockAdaptor {

  /**
   * 尝试获取锁并执行指定操作。
   * 如果成功获取锁，则执行 {@code supplier} 提供的操作并返回其结果。
   * 如果获取锁失败，则返回一个空的 {@link Optional}。
   *
   * @param id 资源ID，用于标识要锁定的资源。
   * @param supplier 要在锁保护下执行的操作。
   * @param <R> 操作返回类型。
   * @return 包含操作返回值的 {@link Optional}，如果获取锁失败则返回 empty。
   */
  <R> Optional<R> tryLock(String id, Supplier<R> supplier);
}
