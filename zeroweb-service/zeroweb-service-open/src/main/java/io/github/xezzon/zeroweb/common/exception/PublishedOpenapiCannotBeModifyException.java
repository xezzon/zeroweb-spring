package io.github.xezzon.zeroweb.common.exception;

/**
 * @author xezzon
 */
public class PublishedOpenapiCannotBeModifyException extends ZerowebRuntimeException {

  public PublishedOpenapiCannotBeModifyException() {
    super(OpenErrorCode.PUBLISHED_OPENAPI_CANNOT_BE_MODIFY);
  }
}
