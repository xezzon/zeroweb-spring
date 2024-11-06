package io.github.xezzon.geom.common.exception;

import io.github.xezzon.geom.core.error.ErrorDetail;
import io.github.xezzon.geom.core.error.ErrorResponse;
import io.github.xezzon.geom.core.error.IErrorCode;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
      Map.entry(NoSuchElementException.class, ErrorCode.NO_SUCH_DATA)
  );

  /**
   * 自发抛出的异常
   */
  @ExceptionHandler(GeomRuntimeException.class)
  public ErrorResponse handleException(GeomRuntimeException e, HttpServletResponse response) {
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
  public ErrorResponse handleException(Throwable e, HttpServletResponse response) {
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
      HttpServletResponse response
  ) {
    IErrorCode errorCode = ErrorCode.ARGUMENT_NOT_VALID;
    response.setStatus(errorCode.sourceType().getResponseCode());
    response.setHeader(ERROR_CODE_HEADER, errorCode.code());
    List<ErrorDetail> details = e.getFieldErrors().parallelStream()
        .map(error -> new ErrorDetail(error.getCode(), error.getDefaultMessage()))
        .toList();
    String errorName = e.getClass().getSimpleName();
    ErrorDetail errorDetail = new ErrorDetail(errorName, errorCode.message(), details);
    return new ErrorResponse(errorCode.code(), errorDetail);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ErrorResponse handleException(NoResourceFoundException e, HttpServletResponse response) {
    ErrorCode errorCode = ErrorCode.NOT_FOUND;
    response.setStatus(HttpResponseStatus.NOT_FOUND.code());
    response.setHeader(ERROR_CODE_HEADER, errorCode.code());
    ErrorDetail errorDetail = new ErrorDetail(errorCode.name(), e.getMessage());
    return new ErrorResponse(errorCode.code(), errorDetail);
  }

  protected IErrorCode getErrorCode(Throwable e) {
    return ERROR_CODE_MAP.getOrDefault(e.getClass(), ErrorCode.UNKNOWN);
  }
}
