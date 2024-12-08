package io.github.xezzon.geom.common.exception;

/**
 * @author xezzon
 */
public class PublishedOpenapiCannotBeModifyException extends GeomRuntimeException {

  public PublishedOpenapiCannotBeModifyException() {
    super(OpenErrorCode.PUBLISHED_OPENAPI_CANNOT_BE_MODIFY);
  }
}
