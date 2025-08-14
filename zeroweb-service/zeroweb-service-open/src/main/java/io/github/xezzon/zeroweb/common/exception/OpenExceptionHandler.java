package io.github.xezzon.zeroweb.common.exception;

import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;

import io.github.xezzon.zeroweb.subscription.exception.UnsubscribeOpenapiException;
import io.github.xezzon.zeroweb.third_party_app.exception.InvalidAccessKeyException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author xezzon
 */
@RestControllerAdvice
public class OpenExceptionHandler extends GlobalExceptionHandler {

  /**
   * 无效的访问密钥
   */
  @ExceptionHandler(InvalidAccessKeyException.class)
  public ResponseEntity<ErrorResult> handleException(
      InvalidAccessKeyException e,
      HttpServletRequest request
  ) {
    log(e, request);
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .header(ERROR_CODE_HEADER, e.getCode())
        .body(new ErrorResult(e));
  }

  /**
   * 不能调用未订阅的接口
   */
  @ExceptionHandler(UnsubscribeOpenapiException.class)
  public ResponseEntity<ErrorResult> handleException(
      UnsubscribeOpenapiException e,
      HttpServletRequest request
  ) {
    log(e, request);
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .header(ERROR_CODE_HEADER, e.getCode())
        .body(new ErrorResult(e));
  }
}
