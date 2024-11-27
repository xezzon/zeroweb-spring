package io.github.xezzon.geom.common.otlp;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
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

  private static final String INSTRUMENT_SCOPE_NAME = "io.github.xezzon.geom";
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
    try (Scope ignore = span.makeCurrent()) {
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
