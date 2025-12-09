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

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.Status;
import io.grpc.StatusException;
import io.opentelemetry.api.trace.Span;
import jakarta.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/// gRPC 服务端异常处理器。
///
/// 用于捕获 gRPC 调用过程中抛出的异常，并将其转换为统一的 gRPC `StatusException` 格式，同时附带业务错误码。
@Component
@Slf4j
public class GrpcServerExceptionHandler implements GrpcExceptionHandler {

  /// 定义 gRPC 响应头中用于传递业务错误码的 Key。
  public static final Key<String> ERROR_CODE =
      Key.of("X-Error-Code" + Metadata.BINARY_HEADER_SUFFIX, new Utf8Marshaller());

  /// 处理 gRPC 异常，并将其转换为 `StatusException`。
  ///
  /// 该方法会根据不同的异常类型，映射到不同的 gRPC `Status` 和业务错误码。
  /// 对于未知的异常，默认映射为 `Status.UNKNOWN` 和 `ErrorCodeConstant.UNKNOWN`。
  ///
  /// @param throwable 捕获到的异常。
  /// @return 转换后的 `StatusException`。
  @Override
  public StatusException handleException(final Throwable throwable) {
    log.warn("Request processing failed.", throwable);
    Span.current().recordException(throwable);

    return switch (throwable) {
      case ZerowebBusinessException e -> newStatusException(Status.INVALID_ARGUMENT, e.code());
      case MethodArgumentNotValidException _, HandlerMethodValidationException _ ->
          newStatusException(Status.INVALID_ARGUMENT, ErrorCodeConstant.ARGUMENT_INVALID);
      case NotLoginException _ ->
          newStatusException(Status.UNAUTHENTICATED, ErrorCodeConstant.UNAUTHENTICATED);
      case NotRoleException _, NotPermissionException _ ->
          newStatusException(Status.PERMISSION_DENIED, ErrorCodeConstant.UNAUTHORIZED);
      case EntityNotFoundException _, NoSuchElementException _ ->
          newStatusException(Status.INVALID_ARGUMENT, ErrorCodeConstant.NO_SUCH_DATA);
      default -> newStatusException(Status.UNKNOWN, ErrorCodeConstant.UNKNOWN);
    };
  }

  /// 创建一个新的 `StatusException` 实例，附带指定的 gRPC 状态和业务错误码。
  ///
  /// 业务错误码会通过 gRPC 的 `Metadata` 传递。
  ///
  /// @param status gRPC 状态。
  /// @param errorCode 业务错误码。
  /// @return 包含指定状态和错误码的 `StatusException` 实例。
  private StatusException newStatusException(final Status status, final String errorCode) {
    Metadata metadata = new Metadata();
    metadata.put(ERROR_CODE, errorCode);
    return new StatusException(status, metadata);
  }
}
