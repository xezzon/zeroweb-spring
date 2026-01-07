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

package io.github.xezzon.zeroweb.common.exception;

import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import io.opentelemetry.api.trace.Span;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理，统一管理应用程序中抛出的各种异常，并将其转换为统一的 {@link ErrorResult} 响应格式。
 * 通过 {@link RestControllerAdvice} 捕获所有控制器抛出的异常，并根据异常类型进行相应的处理，
 * 包括业务异常、参数校验异常、认证授权异常以及其他未捕获的运行时异常。
 *
 * @author xezzon
 * @see ZerowebBusinessException
 * @see ErrorResult
 * @see RestControllerAdvice
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * 处理 {@link ZerowebBusinessException 业务异常}。
   * 当业务逻辑中发生可预期的错误时，抛出此异常，并返回自定义的 HTTP 状态码和错误信息。
   *
   * @param e       业务异常实例
   * @param request 当前的 HTTP 请求
   * @return 包含错误详情的 {@link ErrorResult} 响应实体
   */
  @ExceptionHandler(ZerowebBusinessException.class)
  public ResponseEntity<ErrorResult> handleException(
      ZerowebBusinessException e,
      HttpServletRequest request
  ) {
    log(e, request);
    return ResponseEntity
        .status(e.httpStatus())
        .header(ERROR_CODE_HEADER, e.code())
        .body(new ErrorResult(e));
  }

  /**
   * 处理所有未被特定异常处理器捕获的 {@link Throwable 异常}。
   * 作为兜底的异常处理机制，捕获所有非业务异常，返回通用的服务器错误信息。
   *
   * @param e       异常实例
   * @param request 当前的 HTTP 请求
   * @return 包含错误详情的 {@link ErrorResult} 响应实体，HTTP 状态码为 500
   */
  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ErrorResult> handleException(
      Throwable e,
      HttpServletRequest request
  ) {
    log(e, request);
    return ResponseEntity
        .status(ErrorCodeConstant.SERVER_ERROR_STATUS)
        .header(ERROR_CODE_HEADER, ErrorCodeConstant.UNKNOWN)
        .body(new ErrorResult(e));
  }

  /**
   * 处理 {@link NoResourceFoundException 请求资源不存在异常}。
   * 当请求的资源（如静态文件、API 访问点）不存在时，捕获此异常。
   * 此方法会重新抛出异常，让 Spring Boot 默认的 {@code BasicErrorController} 或其他处理器接管。
   *
   * @param e       资源未找到异常实例
   * @param request 当前的 HTTP 请求
   * @throws NoResourceFoundException 重新抛出原始异常
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public void handleException(
      NoResourceFoundException e,
      HttpServletRequest request
  ) throws NoResourceFoundException {
    log(e, request);
    throw e;
  }

  /**
   * 处理 {@link NotLoginException 未登录异常}。
   * 当用户未通过身份认证访问受保护资源时，捕获此异常。
   *
   * @param e       未登录异常实例
   * @param request 当前的 HTTP 请求
   * @return 包含错误详情的 {@link ErrorResult} 响应实体，HTTP 状态码为 401
   */
  @ExceptionHandler(NotLoginException.class)
  public ResponseEntity<ErrorResult> handleException(
      NotLoginException e,
      HttpServletRequest request
  ) {
    log(e, request);
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .header(ERROR_CODE_HEADER, ErrorCodeConstant.UNAUTHENTICATED)
        .body(new ErrorResult(e));
  }

  /**
   * 处理 {@link NotRoleException 角色不足异常} 或 {@link NotPermissionException 权限不足异常}。
   * 当用户已登录但没有足够的角色或权限访问特定资源时，捕获此异常。
   *
   * @param e       运行时异常实例 ({@link NotRoleException} 或 {@link NotPermissionException})
   * @param request 当前的 HTTP 请求
   * @return 包含错误详情的 {@link ErrorResult} 响应实体，HTTP 状态码为 403
   */
  @ExceptionHandler({NotRoleException.class, NotPermissionException.class})
  public ResponseEntity<ErrorResult> handleForbiddenException(
      RuntimeException e,
      HttpServletRequest request
  ) {
    log(e, request);
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .header(ERROR_CODE_HEADER, ErrorCodeConstant.UNAUTHORIZED)
        .body(new ErrorResult(e));
  }

  /**
   * 处理 {@link EntityNotFoundException 实体未找到异常} 或 {@link NoSuchElementException 元素不存在异常}。
   * 当尝试访问数据库中不存在的实体或集合中不存在的元素时，捕获此异常。
   *
   * @param e       运行时异常实例 ({@link EntityNotFoundException} 或 {@link NoSuchElementException})
   * @param request 当前的 HTTP 请求
   * @return 包含错误详情的 {@link ErrorResult} 响应实体，HTTP 状态码为 400
   */
  @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
  public ResponseEntity<ErrorResult> handleDataNotExistException(
      RuntimeException e,
      HttpServletRequest request
  ) {
    log(e, request);
    return ResponseEntity
        .status(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .header(ERROR_CODE_HEADER, ErrorCodeConstant.NO_SUCH_DATA)
        .body(new ErrorResult(e));
  }

  /**
   * 处理 {@link MethodArgumentNotValidException 参数校验不通过异常}。
   * 当请求参数不符合校验规则时，捕获此异常，并返回详细的参数错误信息。
   *
   * @param e       参数校验异常实例
   * @param request 当前的 HTTP 请求
   * @return 包含参数错误详情的 {@link ErrorResult} 响应实体，HTTP 状态码为 400
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResult> handleException(
      MethodArgumentNotValidException e,
      HttpServletRequest request
  ) {
    log(e, request, Level.INFO);
    List<ErrorResult.Detail> errorDetails = e.getFieldErrors().stream()
        .map(error -> {
          Map<String, Object> parameters = new HashMap<>(Map.ofEntries(
              Map.entry("field", error.getField())
          ));
          try {
            ConstraintViolationImpl<?> source = error.unwrap(ConstraintViolationImpl.class);
            parameters.putAll(source.getMessageParameters());
          } catch (IllegalArgumentException _) {
            // 如果不是采用 Hibernate Validator 实现，则不添加消息参数
          }
          return new ErrorResult.Detail(
              Objects.requireNonNullElse(error.getCode(), "Invalid error code."),
              Objects.requireNonNullElse(error.getDefaultMessage(), "Unknown reason."),
              parameters
          );
        })
        .toList();
    return ResponseEntity
        .status(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .header(ERROR_CODE_HEADER, ErrorCodeConstant.ARGUMENT_INVALID)
        .body(new ErrorResult(e, errorDetails));
  }

  /**
   * 处理 {@link HandlerMethodValidationException 方法参数验证异常}。
   * 当控制器方法的参数验证失败时，捕获此异常，并返回详细的参数错误信息。
   *
   * @param e       方法参数验证异常实例
   * @param request 当前的 HTTP 请求
   * @return 包含参数错误详情的 {@link ErrorResult} 响应实体，HTTP 状态码为 400
   */
  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResult> handleException(
      HandlerMethodValidationException e,
      HttpServletRequest request
  ) {
    log(e, request, Level.INFO);
    List<ErrorResult.Detail> errorDetails = e.getValueResults().stream()
        .flatMap(result -> {
          String field = result.getMethodParameter().getParameterName();
          return result.getResolvableErrors().stream()
              .map(error -> {
                Map<String, Object> parameter = new HashMap<>(
                    Collections.singletonMap(
                        "field",
                        Objects.requireNonNullElse(field, "Unknown field.")
                    )
                );
                try {
                  ConstraintViolation<?> violation = result
                      .unwrap(error, ConstraintViolation.class);
                  if (violation instanceof ConstraintViolationImpl<?> violationImpl) {
                    parameter.putAll(violationImpl.getMessageParameters());
                  }
                } catch (IllegalArgumentException _) {
                  // 如果不是采用 Hibernate Validator 实现，则不添加消息参数
                }
                String code = Optional.ofNullable(error.getCodes())
                    .filter(codes -> codes.length > 0)
                    .map(codes -> codes[codes.length - 1])
                    .orElse("Invalid error code.");
                return new ErrorResult.Detail(
                    code,
                    Objects.requireNonNullElse(error.getDefaultMessage(), "Unknown reason."),
                    parameter
                );
              });
        })
        .toList();
    return ResponseEntity
        .status(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .header(ERROR_CODE_HEADER, ErrorCodeConstant.ARGUMENT_INVALID)
        .body(new ErrorResult(e, errorDetails));
  }

  /**
   * 处理 {@link HttpRequestMethodNotSupportedException HTTP 请求方法不支持异常}。
   * 当客户端使用了服务器不支持的 HTTP 方法（如 GET、POST 等）时，捕获此异常。
   *
   * @param e       HTTP 请求方法不支持异常实例
   * @param request 当前的 HTTP 请求
   * @return 包含错误详情的 {@link ErrorResult} 响应实体，HTTP 状态码为 405
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResult> handleException(
      HttpRequestMethodNotSupportedException e,
      HttpServletRequest request
  ) {
    log(e, request);
    return ResponseEntity
        .status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(new ErrorResult(e));
  }

  /// 记录异常信息到日志，并记录到 OpenTelemetry Span。
  ///
  /// @param e        异常实例
  /// @param request  当前的 HTTP 请求
  /// @param logLevel 日志级别，如 [Level#INFO], [Level#WARN], [Level#ERROR]
  protected final void log(Throwable e, HttpServletRequest request, Level logLevel) {
    log.atLevel(logLevel).log("Request processing failed: {}", request.getRequestURI(), e);
    Span.current().recordException(e);
  }

  /// 记录异常信息到日志，默认日志级别为 WARN。
  ///
  /// @param e       异常实例
  /// @param request 当前的 HTTP 请求
  protected final void log(Throwable e, HttpServletRequest request) {
    log(e, request, Level.WARN);
  }
}
