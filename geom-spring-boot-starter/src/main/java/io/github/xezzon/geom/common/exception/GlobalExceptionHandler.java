package io.github.xezzon.geom.common.exception;

import io.github.xezzon.tao.exception.ClientException;
import io.github.xezzon.tao.exception.ServerException;
import io.github.xezzon.tao.exception.ThirdPartyException;
import io.github.xezzon.tao.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author xezzon
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  protected final String errorMessage;

  public GlobalExceptionHandler() {
    this("服务器开小差了，请稍后重试");
  }

  protected GlobalExceptionHandler(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @ExceptionHandler(ClientException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public Result<Void> handleClientException(ClientException e) {
    log.trace("客户端错误", e);
    return Result.fail(e);
  }

  @ExceptionHandler(ServerException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleServerException(ServerException e) {
    log.warn("服务端错误", e);
    return Result.fail(e, errorMessage);
  }

  @ExceptionHandler(ThirdPartyException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleThirdPartyException(ThirdPartyException e) {
    log.warn("第三方服务故障", e);
    return Result.fail(e, errorMessage);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleException(Exception e) {
    log.error("未知故障", e);
    return Result.fail(new ServerException(e.getMessage(), e), errorMessage);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleException(MethodArgumentNotValidException e) {
    log.error(ErrorCode.ARGUMENT_NOT_VALID.message(), e);
    return Result.fail(new ArgumentNotValidException(e));
  }
}
