package io.github.xezzon.zeroweb.common.exception;

/**
 * ZeroWeb 自发抛出的系统异常
 * @author xezzon
 */
public class ZerowebRuntimeException extends RuntimeException {

  public ZerowebRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public ZerowebRuntimeException(Throwable cause) {
    super(cause);
  }
}
