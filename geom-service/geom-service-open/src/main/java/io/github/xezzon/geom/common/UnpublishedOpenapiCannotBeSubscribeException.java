package io.github.xezzon.geom.common;

import io.github.xezzon.geom.common.exception.GeomRuntimeException;
import io.github.xezzon.geom.core.error.IErrorCode;

/**
 * @author xezzon
 */
public class UnpublishedOpenapiCannotBeSubscribeException extends GeomRuntimeException {

  private static final IErrorCode ERROR_CODE =
      OpenErrorCode.UNPUBLISHED_OPENAPI_CANNOT_BE_SUBSCRIBE;

  public UnpublishedOpenapiCannotBeSubscribeException() {
    super(ERROR_CODE);
  }
}
