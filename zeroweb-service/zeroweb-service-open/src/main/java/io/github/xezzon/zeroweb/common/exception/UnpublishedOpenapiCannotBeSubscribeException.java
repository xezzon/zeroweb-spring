package io.github.xezzon.zeroweb.common.exception;

import io.github.xezzon.zeroweb.core.error.IErrorCode;

/**
 * @author xezzon
 */
public class UnpublishedOpenapiCannotBeSubscribeException extends ZerowebRuntimeException {

  private static final IErrorCode ERROR_CODE =
      OpenErrorCode.UNPUBLISHED_OPENAPI_CANNOT_BE_SUBSCRIBE;

  public UnpublishedOpenapiCannotBeSubscribeException() {
    super(ERROR_CODE);
  }
}
