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

import com.google.common.util.concurrent.Striped;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

/// 基于 [Striped] 实现的锁提供者，提供本地锁。
///
/// 该类被标记为 [Component] 和 [Fallback]，表明它是一个 Spring 组件，
/// 并且在存在多个 [LockProvider] 实现时可以作为备用。
/// [NullMarked] 注解表明该类中的所有类型（除非另有明确注解）都不能为空。
///
/// @author xezzon
@Component
@Fallback
@NullMarked
public class StripedLock implements LockProvider {

  /// 根据提供的名称和超时时间创建一个 [LockAdaptor] 实例。
  ///
  /// @param name    锁的名称，在此实现中不使用。
  /// @param timeout 尝试获取锁时的超时时间（秒）。
  /// @return 封装了基于 [Striped] 的锁逻辑的 [LockAdaptor] 实例。
  @Override
  public LockAdaptor of(String name, int timeout) {
    return new InnerLock(timeout);
  }

  /// [LockAdaptor] 接口的内部实现，用于处理具体的锁操作。
  /// 它维护一个 [Striped][Striped] 实例，用于为不同的 ID 提供不同的锁。
  public static class InnerLock implements LockAdaptor {

    /**
     * 用于提供本地锁的 {@link Striped Striped<Lock>} 实例。
     * 使用 {@link Striped#lazyWeakLock(int)} 创建，支持弱引用且按需创建锁。
     */
    private final Striped<Lock> stripedLocks = Striped.lazyWeakLock(100);
    /**
     * 尝试获取锁时的超时时间（秒）。
     */
    private final int timeout;

    /// 初始化 [InnerLock] 实例，设置超时时间。
    ///
    /// @param timeout 尝试获取锁时的超时时间（秒）。
    public InnerLock(int timeout) {
      this.timeout = timeout;
    }

    /// 尝试获取与给定 ID 关联的锁。
    ///
    /// 如果在指定的 [#timeout] 时间内成功获取锁，则执行 `supplier` 提供的方法并返回其结果。
    /// 如果获取锁失败（超时），则返回一个空的 [Optional]。
    /// 在操作完成后，无论成功与否，都会释放锁。
    ///
    /// @param id       锁的唯一标识符。
    /// @param supplier 如果成功获取锁，将执行的 [Supplier]。
    /// @param <R>      [Supplier] 返回值的类型。
    /// @return 如果成功获取锁并执行 [Supplier]，则返回包含其结果的 [Optional]；否则返回空的 [Optional]。
    /// @throws ZerowebRuntimeException 如果线程在等待锁的过程中被中断。
    @Override
    public <R> Optional<R> tryLock(String id, Supplier<@Nullable R> supplier) {
      Lock specificLock = stripedLocks.get(id);
      try {
        if (!specificLock.tryLock(timeout, TimeUnit.SECONDS)) {
          return Optional.empty();
        }
        try {
          return Optional.ofNullable(supplier.get());
        } finally {
          specificLock.unlock();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new ZerowebRuntimeException(e);
      }
    }
  }
}
