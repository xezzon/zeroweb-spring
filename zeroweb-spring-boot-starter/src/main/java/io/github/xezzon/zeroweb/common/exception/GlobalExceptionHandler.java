package io.github.xezzon.zeroweb.common.exception;

import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import io.opentelemetry.api.trace.Span;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理
 * @author xezzon
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * 业务异常
   */
  @ExceptionHandler(ZerowebBusinessException.class)
  public ResponseEntity<ErrorResult> handleException(
      ZerowebBusinessException e,
      HttpServletRequest request
  ) {
    log(e, request);
    return ResponseEntity
        .status(e.getHttpStatus())
        .header(ERROR_CODE_HEADER, e.getCode())
        .body(new ErrorResult(e));
  }

  /**
   * 非业务异常（通用）
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
   * 参数校验不通过
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResult> handleException(
      MethodArgumentNotValidException e,
      HttpServletRequest request
  ) {
    log(e, request, Level.INFO);
    List<ErrorResult.Detail> errorDetails = e.getFieldErrors().stream()
        .map(error -> new ErrorResult.Detail(
            error.getCode(),
            error.getDefaultMessage(),
            Map.ofEntries(
                Map.entry("field", error.getField())
            )
        ))
        .toList();
    return ResponseEntity
        .status(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .header(ERROR_CODE_HEADER, ErrorCodeConstant.ARGUMENT_INVALID)
        .body(new ErrorResult(e, errorDetails));
  }

  /**
   * 请求资源不存在
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResult> handleException(
      NoResourceFoundException e,
      HttpServletRequest request
  )
      throws NoResourceFoundException {
    log(e, request);
    throw e;
  }

  /**
   * 未登录
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
   * 接口未授权
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
   * 数据已删除
   */
  @ExceptionHandler({EntityNotFoundException.class})
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

  protected final void log(Throwable e, HttpServletRequest request, Level logLevel) {
    log.atLevel(logLevel).log("Request processing failed: {}", request.getRequestURI(), e);
    Span.current().recordException(e);
  }

  protected final void log(Throwable e, HttpServletRequest request) {
    log(e, request, Level.WARN);
  }
}
