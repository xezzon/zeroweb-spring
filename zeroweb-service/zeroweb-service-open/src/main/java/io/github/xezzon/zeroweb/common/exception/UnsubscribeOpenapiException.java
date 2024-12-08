package io.github.xezzon.zeroweb.common.exception;

import io.github.xezzon.zeroweb.core.error.IErrorCode;

/**
 * 不能调用未订阅的接口
 * @author xezzon
 */
public class UnsubscribeOpenapiException extends ZerowebRuntimeException {

  private static final IErrorCode ERROR_CODE = OpenErrorCode.UNSUBSCRIBED_OPENAPI;

  public UnsubscribeOpenapiException() {
    super(ERROR_CODE);
  }
}
