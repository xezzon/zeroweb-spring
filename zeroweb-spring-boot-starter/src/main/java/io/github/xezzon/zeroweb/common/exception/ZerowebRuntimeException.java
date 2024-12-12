package io.github.xezzon.zeroweb.common.exception;

import io.github.xezzon.zeroweb.core.error.IErrorCode;

/**
 * ZeroWeb系统自发抛出的异常
 * @author xezzon
 */
public class ZerowebRuntimeException extends RuntimeException {

  private final transient IErrorCode errorCode;

  public ZerowebRuntimeException(IErrorCode errorCode, Object... args) {
    super(formatMessage(errorCode, args));
    this.errorCode = errorCode;
  }

  public ZerowebRuntimeException(IErrorCode errorCode, Throwable cause, Object... args) {
    super(formatMessage(errorCode, args), cause);
    this.errorCode = errorCode;
  }

  public static String formatMessage(IErrorCode errorCode, Object... args) {
    return String.format(errorCode.message(), args);
  }

  public IErrorCode errorCode() {
    return this.errorCode;
  }
}
