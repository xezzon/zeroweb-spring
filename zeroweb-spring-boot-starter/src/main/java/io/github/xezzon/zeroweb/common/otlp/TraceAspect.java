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

/**
 * 使用切面添加 OpenTelemetry Trace
 * @author xezzon
 */
@Component
@Aspect
public class TraceAspect {

  private static final String INSTRUMENT_SCOPE_NAME = "io.github.xezzon.zeroweb";
  private static final String CODE_NAMESPACE_KEY = "code.namespace";
  private static final String CODE_FUNCTION_KEY = "code.function";

  /**
   * 为Service类的方法添加OpenTelemetry追踪
   */
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
