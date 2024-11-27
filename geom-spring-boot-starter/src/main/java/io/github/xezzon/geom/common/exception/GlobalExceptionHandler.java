package io.github.xezzon.geom.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import io.github.xezzon.geom.core.error.ErrorDetail;
import io.github.xezzon.geom.core.error.ErrorResponse;
import io.github.xezzon.geom.core.error.IErrorCode;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.opentelemetry.api.trace.Span;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * @author xezzon
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  public static final String ERROR_CODE_HEADER = "X-Error-Code";
  private static final Map<Class<? extends Throwable>, IErrorCode> ERROR_CODE_MAP = Map.ofEntries(
      Map.entry(EntityNotFoundException.class, ErrorCode.NO_SUCH_DATA)
  );

  /**
   * 自发抛出的异常
   */
  @ExceptionHandler(GeomRuntimeException.class)
  public ErrorResponse handleException(
      GeomRuntimeException e,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    log(e, request);
    IErrorCode errorCode = e.errorCode();
    response.setStatus(errorCode.sourceType().getResponseCode());
    response.setHeader(ERROR_CODE_HEADER, errorCode.code());
    ErrorDetail errorDetail = new ErrorDetail(errorCode.name(), e.getMessage());
    return new ErrorResponse(errorCode.code(), errorDetail);
  }

  /**
   * 框架抛出的异常
   */
  @ExceptionHandler(Throwable.class)
  public ErrorResponse handleException(
      Throwable e,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    log(e, request);
    IErrorCode errorCode = this.getErrorCode(e);
    response.setStatus(errorCode.sourceType().getResponseCode());
    response.setHeader(ERROR_CODE_HEADER, errorCode.code());
    ErrorDetail errorDetail = new ErrorDetail(e.getClass().getSimpleName(), errorCode.message());
    return new ErrorResponse(errorCode.code(), errorDetail);
  }

  /**
   * 参数校验不通过
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponse handleException(
      MethodArgumentNotValidException e,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    log(e, request);
    IErrorCode errorCode = ErrorCode.ARGUMENT_NOT_VALID;
    response.setStatus(errorCode.sourceType().getResponseCode());
    response.setHeader(ERROR_CODE_HEADER, errorCode.code());
    List<ErrorDetail> details = e.getFieldErrors().parallelStream()
        .map(error -> new ErrorDetail(error.getCode(), error.getDefaultMessage()))
        .toList();
    String errorName = e.getClass().getSimpleName();
    ErrorDetail errorDetail = new ErrorDetail(errorName, errorCode.message())
        .setDetails(details);
    return new ErrorResponse(errorCode.code(), errorDetail);
  }

  /**
   * 请求资源不存在
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ErrorResponse handleException(
      NoResourceFoundException e,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    log(e, request);
    ErrorCode errorCode = ErrorCode.NOT_FOUND;
    response.setStatus(HttpResponseStatus.NOT_FOUND.code());
    response.setHeader(ERROR_CODE_HEADER, errorCode.code());
    ErrorDetail errorDetail = new ErrorDetail(errorCode.name(), e.getMessage());
    return new ErrorResponse(errorCode.code(), errorDetail);
  }

  /**
   * 未登录
   */
  @ExceptionHandler(NotLoginException.class)
  public ErrorResponse handleException(
      NotLoginException e,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    log(e, request);
    ErrorCode errorCode = ErrorCode.NOT_LOGIN;
    response.setStatus(HttpResponseStatus.UNAUTHORIZED.code());
    response.setHeader(ERROR_CODE_HEADER, errorCode.code());
    ErrorDetail errorDetail = new ErrorDetail(errorCode.name(), errorCode.message());
    return new ErrorResponse(errorCode.code(), errorDetail);
  }

  /**
   * 数据权限不足
   */
  @ExceptionHandler(DataPermissionForbiddenException.class)
  public ErrorResponse handleException(
      DataPermissionForbiddenException e,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    log(e, request);
    IErrorCode errorCode = e.errorCode();
    response.setStatus(HttpResponseStatus.FORBIDDEN.code());
    response.setHeader(ERROR_CODE_HEADER, errorCode.code());
    ErrorDetail errorDetail = new ErrorDetail(errorCode.name(), e.getMessage());
    return new ErrorResponse(errorCode.code(), errorDetail);
  }

  protected IErrorCode getErrorCode(Throwable e) {
    return ERROR_CODE_MAP.getOrDefault(e.getClass(), ErrorCode.UNKNOWN);
  }

  protected void log(Throwable e, HttpServletRequest request) {
    log.error("Request processing failed: {}", request.getRequestURI(), e);
    Span.current().recordException(e);
  }
}
