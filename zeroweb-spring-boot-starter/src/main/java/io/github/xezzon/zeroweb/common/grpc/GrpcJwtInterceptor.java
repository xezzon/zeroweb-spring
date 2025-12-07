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

package io.github.xezzon.zeroweb.common.grpc;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

import com.google.protobuf.InvalidProtocolBufferException;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.client.GlobalClientInterceptor;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

/// `GrpcJwtInterceptor` 用于在 gRPC 服务间调用中传递 JWT 认证信息。
/// 它作为一个全局服务端拦截器和全局客户端拦截器，确保 JWT `Claim` 在调用链中正确地传递。
///
/// 作为服务端拦截器，它从传入的 `Metadata` 中解析 JWT `Claim`，并将其绑定到当前的 gRPC `Context` 中。
/// 这使得下游服务能够访问认证信息。
///
/// 作为客户端拦截器，它从当前的 `Context` 中获取 JWT `Claim`，并将其添加到出站请求的 `Metadata` 中。
/// 这确保了对其他服务的调用能够携带认证信息。
///
/// @author xezzon
@GlobalServerInterceptor
@GlobalClientInterceptor
@Component
@Slf4j
public class GrpcJwtInterceptor implements ServerInterceptor, ClientInterceptor {

  private static final Key<byte[]> BEARER = Key.of(
      AUTHORIZATION + Metadata.BINARY_HEADER_SUFFIX,
      Metadata.BINARY_BYTE_MARSHALLER
  );

  /// 服务端拦截器，用于在 gRPC 服务接收请求时处理认证信息。
  /// 它从请求头中提取 JWT `Claim`，并将其设置到当前的 `Context` 中，
  /// 以便在后续的处理中能够访问认证信息。
  ///
  /// @param call gRPC `ServerCall` 对象，代表当前的服务器调用。
  /// @param headers 传入的 `Metadata` 头部信息，包含认证令牌。
  /// @param next `ServerCallHandler`，用于继续处理请求。
  /// @param <T> 请求消息的类型。
  /// @param <R> 响应消息的类型。
  /// @return `ServerCall.Listener`，用于监听请求事件。
  @Override
  public <T, R> Listener<T> interceptCall(
      final ServerCall<T, R> call,
      final Metadata headers,
      final ServerCallHandler<T, R> next
  ) {
    JwtClaim claim = null;
    try {
      final byte[] jwtClaimBytes = headers.get(BEARER);
      if (jwtClaimBytes != null) {
        claim = JwtClaim.parseFrom(jwtClaimBytes);
      }
    } catch (RuntimeException | InvalidProtocolBufferException e) {
      log.error("Parse JWT failed.", e);
    }
    Context context = Context.current().withValue(JwtAuth.CONTEXT, claim);
    return Contexts.interceptCall(context, call, headers, next);
  }

  /// 客户端拦截器，用于在 gRPC 客户端发起请求时添加认证信息。
  /// 它从当前的 `Context` 中获取 JWT `Claim`，并将其添加到出站请求的 `Metadata` 中，
  /// 确保请求携带认证信息。
  ///
  /// @param method `MethodDescriptor`，代表调用的方法。
  /// @param callOptions `CallOptions`，调用选项。
  /// @param next `Channel`，用于发起新的调用。
  /// @param <T> 请求消息的类型。
  /// @param <R> 响应消息的类型。
  /// @return `ClientCall`，用于发起客户端调用。
  @Override
  public <T, R> ClientCall<T, R> interceptCall(
      final MethodDescriptor<T, R> method,
      final CallOptions callOptions,
      final Channel next
  ) {
    return new SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
      @Override
      public void start(Listener<R> responseListener, Metadata headers) {
        JwtAuth.get().ifPresent(claim ->
            headers.put(BEARER, claim.toByteArray())
        );
        super.start(responseListener, headers);
      }
    };
  }
}
