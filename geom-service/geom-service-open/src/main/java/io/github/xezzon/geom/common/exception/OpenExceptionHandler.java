package io.github.xezzon.geom.common.exception;

import io.github.xezzon.geom.core.error.ErrorDetail;
import io.github.xezzon.geom.core.error.ErrorResponse;
import io.github.xezzon.geom.core.error.IErrorCode;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author xezzon
 */
@RestControllerAdvice
@Slf4j
public class OpenExceptionHandler extends GlobalExceptionHandler {

  /**
   * 没有订阅的接口
   */
  @ExceptionHandler(UnsubscribeOpenapiException.class)
  public ErrorResponse handleException(
      UnsubscribeOpenapiException e,
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

  /**
   * 使用的AccessKey或SecretKey不存在或错误或不匹配
   */
  @ExceptionHandler(InvalidAccessKeyException.class)
  public ErrorResponse handleException(
      InvalidAccessKeyException e,
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
}
