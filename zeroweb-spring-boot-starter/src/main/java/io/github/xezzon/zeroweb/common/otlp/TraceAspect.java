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

package io.github.xezzon.zeroweb.common.otlp;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/// 此切面用于通过 OpenTelemetry 为服务方法自动添加分布式追踪。
/// 它拦截所有被 Spring [@Service][org.springframework.stereotype.Service] 注解的类中的方法，
/// 并为每个方法创建一个新的 Span，记录方法的执行信息，包括命名空间、类名和方法名。
/// 异常发生时，Span 会记录异常信息，并在方法执行结束时（无论成功或失败）关闭 Span。
///
/// @author xezzon
@Component
@Aspect
public class TraceAspect {

  /// OpenTelemetry Tracer 的仪表作用域名称。
  /// 此名称用于标识生成 Span 的应用程序或库。
  private static final String INSTRUMENT_SCOPE_NAME = "io.github.xezzon.zeroweb";
  /// 用于存储代码命名空间的 Span 属性键。
  /// 通常是方法所在的完整类名。
  private static final String CODE_NAMESPACE_KEY = "code.namespace";
  /// 用于存储代码函数名称的 Span 属性键。
  /// 通常是方法的名称。
  private static final String CODE_FUNCTION_KEY = "code.function";

  /// 拦截所有被 [@Service][org.springframework.stereotype.Service] 注解的类中的方法，
  /// 并为这些方法的执行添加 OpenTelemetry 追踪。
  ///
  /// 每个被追踪的方法将创建一个新的 Span。Span 的名称由类名和方法名组合而成，
  /// 并设置 `code.namespace` 和 `code.function` 属性。
  /// 如果方法执行成功，Span 状态设置为 [StatusCode#OK]。
  /// 如果方法执行抛出异常，异常信息会被记录到 Span 中，然后异常会被重新抛出。
  /// 无论方法执行结果如何，Span 都会在方法执行结束时关闭。
  ///
  ///
  /// @param pjp 封装了被拦截方法的执行点信息，通过它可以调用目标方法。
  /// @return 目标方法的执行结果。
  /// @throws Throwable 如果目标方法抛出任何异常，此异常会被重新抛出。
  @Around("@within(org.springframework.stereotype.Service)")
  public Object traceServiceMethods(ProceedingJoinPoint pjp) throws Throwable {
    String namespace = pjp.getSignature().getDeclaringTypeName();
    String className = pjp.getSignature().getDeclaringType().getSimpleName();
    String methodName = pjp.getSignature().getName();
    Span span = GlobalOpenTelemetry.getTracer(INSTRUMENT_SCOPE_NAME)
        .spanBuilder(className + "." + methodName)
        .setAttribute(CODE_NAMESPACE_KEY, namespace)
        .setAttribute(CODE_FUNCTION_KEY, methodName)
        .startSpan();
    try (var _ = span.makeCurrent()) {
      Object result = pjp.proceed();
      span.setStatus(StatusCode.OK);
      return result;
    } catch (Throwable e) {
      span.recordException(e);
      throw e;
    } finally {
      span.end();
    }
  }
}
