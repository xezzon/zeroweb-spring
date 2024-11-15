package io.github.xezzon.geom.common;

import io.github.xezzon.geom.common.exception.GeomRuntimeException;

/**
 * @author xezzon
 */
public class PublishedOpenapiCannotBeModifyException extends GeomRuntimeException {

  public PublishedOpenapiCannotBeModifyException() {
    super(OpenErrorCode.PUBLISHED_OPENAPI_CANNOT_BE_MODIFY);
  }
}
