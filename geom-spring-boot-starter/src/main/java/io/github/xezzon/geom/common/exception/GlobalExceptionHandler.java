package io.github.xezzon.geom.common.exception;

import io.github.xezzon.tao.exception.ClientException;
import io.github.xezzon.tao.exception.ServerException;
import io.github.xezzon.tao.exception.ThirdPartyException;
import io.github.xezzon.tao.web.Result;
import jakarta.servlet.ServletRequest;
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

  /**
   * 初始化全局异常处理器（服务端错误使用默认消息）
   */
  public GlobalExceptionHandler() {
    this("服务器开小差了，请稍后重试");
  }

  /**
   * 初始化全局异常处理器
   * @param errorMessage 服务端错误默认消息
   */
  protected GlobalExceptionHandler(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * 处理客户端异常
   * @param e 客户端错误
   * @return 统一异常消息
   */
  @ExceptionHandler(ClientException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public Result<Void> handleClientException(ClientException e) {
    log.trace("客户端错误", e);
    return Result.fail(e);
  }

  /**
   * 处理服务端错误
   * @param e 服务端错误
   * @return 统一异常消息
   */
  @ExceptionHandler(ServerException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleServerException(ServerException e) {
    log.warn("服务端错误", e);
    return Result.fail(e, errorMessage);
  }

  /**
   * 处理第三方服务故障
   * @param e 第三方服务故障
   * @return 统一异常消息
   */
  @ExceptionHandler(ThirdPartyException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleThirdPartyException(ThirdPartyException e) {
    log.warn("第三方服务故障", e);
    return Result.fail(e, errorMessage);
  }

  /**
   * 默认捕获所有未知的故障
   * @param e 未知故障
   * @return 统一异常消息
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleException(Exception e, ServletRequest request) {
    log.error("未知故障", e);
    return Result.fail(new ServerException(e.getMessage(), e), errorMessage);
  }

  /**
   * HTTP接口请求参数异常
   * @param e 异常信息
   * @return 统一异常消息
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleException(MethodArgumentNotValidException e) {
    log.error(ErrorCode.ARGUMENT_NOT_VALID.message(), e);
    return Result.fail(new ArgumentNotValidException(e));
  }
}
