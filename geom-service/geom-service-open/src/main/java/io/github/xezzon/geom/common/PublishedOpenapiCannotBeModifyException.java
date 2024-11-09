package io.github.xezzon.geom.common;

import io.github.xezzon.tao.exception.ClientException;

/**
 * @author xezzon
 */
public class PublishedOpenapiCannotBeModifyException extends ClientException {

  public PublishedOpenapiCannotBeModifyException() {
    super(
        OpenErrorCode.PUBLISHED_OPENAPI_CANNOT_BE_MODIFY.code(),
        OpenErrorCode.PUBLISHED_OPENAPI_CANNOT_BE_MODIFY.message()
    );
  }

  public PublishedOpenapiCannotBeModifyException(String message) {
    super(OpenErrorCode.PUBLISHED_OPENAPI_CANNOT_BE_MODIFY.code(), message);
  }
}
