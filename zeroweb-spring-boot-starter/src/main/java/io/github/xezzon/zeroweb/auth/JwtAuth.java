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

package io.github.xezzon.zeroweb.auth;

import static cn.dev33.satoken.exception.NotLoginException.DEFAULT_MESSAGE;
import static cn.dev33.satoken.exception.NotLoginException.NOT_TOKEN;

import cn.dev33.satoken.exception.NotLoginException;
import io.grpc.Context;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

/// JWT 认证工具类，用于管理和获取当前线程的 JWT 认证信息。
/// 支持从 `ScopedValue` 和 gRPC `Context` 中获取认证数据。
/// @author xezzon
public class JwtAuth {

  /// ScopedValue 用于在当前线程范围内存储 JWT 认证信息。
  public static final ScopedValue<JwtClaim> CLAIM = ScopedValue.newInstance();
  /// gRPC Context.Key 用于在 gRPC 请求上下文中存储 JWT 认证信息。
  public static final Context.Key<JwtClaim> CONTEXT = Context.key("JwtClaim");

  private JwtAuth() {
  }

  /// 获取当前线程的 JWT 认证信息。
  /// 优先从 gRPC Context 中获取，如果不存在，则从 ScopedValue 中获取。
  /// 如果两种方式都未获取到，则返回 [Optional#empty()]。
  /// @return 包含 JWT 认证信息的 Optional 对象，如果不存在则为 empty。
  public static @NonNull Optional<JwtClaim> get() {
    JwtClaim grpcContext = CONTEXT.get();
    if (grpcContext != null) {
      return Optional.of(grpcContext);
    }
    if (!CLAIM.isBound()) {
      return Optional.empty();
    }
    return Optional.ofNullable(CLAIM.get());
  }

  /// 获取当前线程的 JWT 认证信息，如果不存在则抛出异常。
  /// 优先从 gRPC Context 中获取，如果不存在，则从 ScopedValue 中获取。
  /// @return JWT 认证信息。
  /// @throws NotLoginException 如果当前线程没有认证信息。
  public static @NonNull JwtClaim getOrThrow() {
    return get()
        .orElseThrow(() ->
            new NotLoginException(DEFAULT_MESSAGE, null, NOT_TOKEN)
        );
  }
}
