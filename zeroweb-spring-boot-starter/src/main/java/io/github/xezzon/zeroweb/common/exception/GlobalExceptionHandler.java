package io.github.xezzon.zeroweb.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import io.github.xezzon.zeroweb.common.i18n.I18nUtil;
import io.github.xezzon.zeroweb.core.error.IErrorCode;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.opentelemetry.api.trace.Span;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理
 * 错误码: 依据`异常类-错误码`映射查找。
 * HTTP 状态码: 客户端错误返回40x，服务端错误返回500。
 * 异常名称: 异常类的简写名。
 * 异常消息: 客户端异常的异常消息由错误码国际化（语言由 HTTP 请求头定义）得到。服务端异常则返回统一的消息，以便向客户端隐藏细节。
 * 日志：通常的异常日志级别为 WARN，部分异常可视情况提高或降低日志级别。日志的异常消息取自 {@link Throwable#getMessage()}，自行实现的异常会依据异常类名对内容进行国际化。
 * @author xezzon
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * 错误码的请求头名称
   */
  public static final String ERROR_CODE_HEADER = "X-Error-Code";

  /**
   * 业务异常
   */
  @ExceptionHandler(ZerowebBusinessException.class)
  public ResponseEntity<ErrorResponse> handleException(
      ZerowebBusinessException e,
      HttpServletRequest request
  ) {
    log(e, request);
    // 错误码
    IErrorCode errorCode = this.getErrorCode(e);
    // 响应码
    int responseStatus = errorCode.sourceType().getResponseCode();
    // 异常名称
    String errorName = e.getClass().getSimpleName();
    // 异常消息
    String errorMessage = e.getLocalizedMessage(request.getLocale(), errorCode.name());
    // 异常明细
    ErrorDetail errorDetail = new ErrorDetail(errorName, errorMessage);
    // 响应体
    return ResponseEntity
        .status(responseStatus)
        .header(ERROR_CODE_HEADER, errorCode.code())
        .body(new ErrorResponse(errorCode.code(), errorDetail));
  }

  /**
   * 非业务异常（通用）
   */
  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ErrorResponse> handleException(
      Throwable e,
      HttpServletRequest request
  ) {
    log(e, request);
    // 错误码
    IErrorCode errorCode = this.getErrorCode(e);
    // 响应码
    int responseStatus = errorCode.sourceType().getResponseCode();
    // 异常名称
    String errorName = e.getClass().getSimpleName();
    // 异常消息
    String errorMessage = I18nUtil.formatter(IErrorCode.I18N_BASENAME)
        .locale(request.getLocale())
        .format(errorCode.name(), CommonErrorCode.UNKNOWN.name());
    // 异常明细
    ErrorDetail errorDetail = new ErrorDetail(errorName, errorMessage);
    // 响应体
    return ResponseEntity
        .status(responseStatus)
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
    // 参数校验错误，降低日志级别
    log(e, request, Level.INFO);
    // 错误码
    IErrorCode errorCode = this.getErrorCode(e);
    // 响应码
    int responseStatus = HttpResponseStatus.BAD_REQUEST.code();
    // 异常名称
    String errorName = e.getClass().getSimpleName();
    // 异常消息
    String errorMessage = I18nUtil.formatter(IErrorCode.I18N_BASENAME)
        .locale(request.getLocale())
        .format(errorCode.name(), CommonErrorCode.UNKNOWN.name());
    // 异常明细
    ErrorDetail errorDetail = new ErrorDetail(errorName, errorMessage);
    errorDetail.setDetails(e.getFieldErrors().parallelStream()
        .map(error -> new ErrorDetail(error.getField(), error.getDefaultMessage()))
        .toList()
    );
    // 响应体
    return ResponseEntity
        .status(responseStatus)
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
    // 错误码
    IErrorCode errorCode = this.getErrorCode(e);
    // 响应码
    int responseStatus = HttpResponseStatus.NOT_FOUND.code();
    // 异常名称
    String errorName = e.getClass().getSimpleName();
    // 异常消息
    String errorMessage = I18nUtil.formatter(IErrorCode.I18N_BASENAME)
        .locale(request.getLocale())
        .format(errorCode.name(), CommonErrorCode.UNKNOWN.name());
    // 异常明细
    ErrorDetail errorDetail = new ErrorDetail(errorName, errorMessage);
    return ResponseEntity
        .status(responseStatus)
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
    // 错误码
    IErrorCode errorCode = this.getErrorCode(e);
    // 响应码
    int responseStatus = HttpResponseStatus.UNAUTHORIZED.code();
    // 异常名称
    String errorName = e.getClass().getSimpleName();
    // 异常消息
    String errorMessage = I18nUtil.formatter(IErrorCode.I18N_BASENAME)
        .locale(request.getLocale())
        .format(errorCode.name(), CommonErrorCode.UNKNOWN.name());
    // 异常明细
    ErrorDetail errorDetail = new ErrorDetail(errorName, errorMessage);
    return ResponseEntity
        .status(responseStatus)
        .header(ERROR_CODE_HEADER, errorCode.code())
        .body(new ErrorResponse(errorCode.code(), errorDetail));
  }

  protected IErrorCode getErrorCode(Throwable e) {
    return Optional.of(e.getClass())
        .map(CommonErrorCode::mapping)
        .orElse(CommonErrorCode.UNKNOWN);
  }

  protected final void log(Throwable e, HttpServletRequest request, Level logLevel) {
    log.atLevel(logLevel).log("Request processing failed: {}", request.getRequestURI(), e);
    Span.current().recordException(e);
  }

  protected final void log(Throwable e, HttpServletRequest request) {
    log(e, request, Level.WARN);
  }
}
