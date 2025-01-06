package io.github.xezzon.zeroweb.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import io.github.xezzon.zeroweb.core.error.ErrorDetail;
import io.github.xezzon.zeroweb.core.error.ErrorResponse;
import io.github.xezzon.zeroweb.core.error.IErrorCode;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.opentelemetry.api.trace.Span;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理
 * 错误码: 依据`异常类-错误码`映射查找。
 * {@link <a href="https://developer.mozilla.org/docs/Web/HTTP/Status"></a>}: 由错误码描述。
 * 异常名称: 异常类的简写名。
 * 异常消息: 客户端异常的异常消息由异常名称国际化（语言由 HTTP 请求头定义）得到。服务端异常则返回统一的消息，以便向客户端隐藏细节。
 * 日志：通常的异常日志级别为 WARN，部分异常可视情况提高或降低日志级别。日志的异常消息取自 {@link Throwable#getMessage()}，自行实现的异常会依据异常类名对内容进行国际化。
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
  @ExceptionHandler(ZerowebRuntimeException.class)
  public ResponseEntity<ErrorResponse> handleException(
      ZerowebRuntimeException e,
      HttpServletRequest request
  ) {
    log(e, request);
    IErrorCode errorCode = e.errorCode();
    ErrorDetail errorDetail = new ErrorDetail(errorCode.name(), e.getMessage());
    return ResponseEntity
        .status(errorCode.sourceType().getResponseCode())
        .header(ERROR_CODE_HEADER, errorCode.code())
        .body(new ErrorResponse(errorCode.code(), errorDetail));
  }

  /**
   * 框架抛出的异常
   */
  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ErrorResponse> handleException(
      Throwable e,
      HttpServletRequest request
  ) {
    log(e, request);
    IErrorCode errorCode = this.getErrorCode(e);
    ErrorDetail errorDetail = new ErrorDetail(e.getClass().getSimpleName(), errorCode.message());
    return ResponseEntity
        .status(errorCode.sourceType().getResponseCode())
        .header(ERROR_CODE_HEADER, errorCode.code())
        .body(new ErrorResponse(errorCode.code(), errorDetail));
  }

  /**
   * 参数校验不通过
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleException(
      MethodArgumentNotValidException e,
      HttpServletRequest request
  ) {
    log(e, request);
    IErrorCode errorCode = ErrorCode.ARGUMENT_NOT_VALID;
    List<ErrorDetail> details = e.getFieldErrors().parallelStream()
        .map(error -> new ErrorDetail(error.getCode(), error.getDefaultMessage()))
        .toList();
    String errorName = e.getClass().getSimpleName();
    ErrorDetail errorDetail = new ErrorDetail(errorName, errorCode.message())
        .setDetails(details);
    return ResponseEntity
        .status(errorCode.sourceType().getResponseCode())
        .header(ERROR_CODE_HEADER, errorCode.code())
        .body(new ErrorResponse(errorCode.code(), errorDetail));
  }

  /**
   * 请求资源不存在
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(
      NoResourceFoundException e,
      HttpServletRequest request
  ) {
    log(e, request);
    ErrorCode errorCode = ErrorCode.NOT_FOUND;
    ErrorDetail errorDetail = new ErrorDetail(errorCode.name(), e.getMessage());
    return ResponseEntity
        .status(HttpResponseStatus.NOT_FOUND.code())
        .header(ERROR_CODE_HEADER, errorCode.code())
        .body(new ErrorResponse(errorCode.code(), errorDetail));
  }

  /**
   * 未登录
   */
  @ExceptionHandler(NotLoginException.class)
  public ResponseEntity<ErrorResponse> handleException(
      NotLoginException e,
      HttpServletRequest request
  ) {
    log(e, request);
    ErrorCode errorCode = ErrorCode.NOT_LOGIN;
    ErrorDetail errorDetail = new ErrorDetail(errorCode.name(), errorCode.message());
    return ResponseEntity
        .status(HttpResponseStatus.UNAUTHORIZED.code())
        .header(ERROR_CODE_HEADER, errorCode.code())
        .body(new ErrorResponse(errorCode.code(), errorDetail));
  }

  /**
   * 数据权限不足
   */
  @ExceptionHandler(DataPermissionForbiddenException.class)
  public ResponseEntity<ErrorResponse> handleException(
      DataPermissionForbiddenException e,
      HttpServletRequest request
  ) {
    log(e, request);
    IErrorCode errorCode = e.errorCode();
    ErrorDetail errorDetail = new ErrorDetail(errorCode.name(), e.getMessage());
    return ResponseEntity
        .status(HttpResponseStatus.FORBIDDEN.code())
        .header(ERROR_CODE_HEADER, errorCode.code())
        .body(new ErrorResponse(errorCode.code(), errorDetail));
  }

  protected IErrorCode getErrorCode(Throwable e) {
    return ERROR_CODE_MAP.getOrDefault(e.getClass(), ErrorCode.UNKNOWN);
  }

  protected final void log(Throwable e, HttpServletRequest request) {
    log.error("Request processing failed: {}", request.getRequestURI(), e);
    Span.current().recordException(e);
  }
}
