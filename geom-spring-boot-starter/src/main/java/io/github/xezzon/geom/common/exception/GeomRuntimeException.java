package io.github.xezzon.geom.common.exception;

import io.github.xezzon.geom.core.error.IErrorCode;

/**
 * geom系统自发抛出的异常
 * @author xezzon
 */
public class GeomRuntimeException extends RuntimeException {

  private final transient IErrorCode errorCode;

  public GeomRuntimeException(IErrorCode errorCode, Object... args) {
    super(formatMessage(errorCode, args));
    this.errorCode = errorCode;
  }

  public GeomRuntimeException(IErrorCode errorCode, Throwable cause, Object... args) {
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
