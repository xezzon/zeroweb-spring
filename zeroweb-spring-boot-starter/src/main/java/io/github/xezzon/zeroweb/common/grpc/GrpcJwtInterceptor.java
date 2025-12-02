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

/**
 * 服务间调用传递认证信息
 * @author xezzon
 */
@GlobalServerInterceptor
@GlobalClientInterceptor
@Component
@Slf4j
public class GrpcJwtInterceptor implements ServerInterceptor, ClientInterceptor {

  private static final Key<byte[]> BEARER = Key.of(
      AUTHORIZATION + Metadata.BINARY_HEADER_SUFFIX,
      Metadata.BINARY_BYTE_MARSHALLER
  );

  /**
   * 服务端拦截器
   */
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

  /**
   * 客户端拦截器
   */
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
