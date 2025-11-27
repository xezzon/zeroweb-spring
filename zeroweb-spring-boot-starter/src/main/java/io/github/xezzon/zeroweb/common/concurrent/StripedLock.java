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

/**
 * @author xezzon
 */
@Component
@Fallback
@NullMarked
public class StripedLock implements LockProvider {

  @Override
  public LockAdaptor of(String name, int timeout) {
    return new InnerLock(timeout);
  }

  public static class InnerLock implements LockAdaptor {

    private final Striped<Lock> stripedLocks = Striped.lazyWeakLock(100);
    private final int timeout;

    public InnerLock(int timeout) {
      this.timeout = timeout;
    }

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
