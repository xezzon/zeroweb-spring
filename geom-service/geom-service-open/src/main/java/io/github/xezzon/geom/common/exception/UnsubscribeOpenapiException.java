package io.github.xezzon.geom.common.exception;

import io.github.xezzon.geom.core.error.IErrorCode;

/**
 * 不能调用未订阅的接口
 * @author xezzon
 */
public class UnsubscribeOpenapiException extends GeomRuntimeException {

  private static final IErrorCode ERROR_CODE = OpenErrorCode.UNSUBSCRIBED_OPENAPI;

  public UnsubscribeOpenapiException() {
    super(ERROR_CODE);
  }
}
