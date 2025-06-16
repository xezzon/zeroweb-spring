package io.github.xezzon.zeroweb.openapi.exception;

import io.github.xezzon.zeroweb.common.exception.OpenErrorCode;
import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import java.util.Collections;

/**
 * @author xezzon
 */
public class PublishedOpenapiCannotBeModifyException extends ZerowebBusinessException {

  public PublishedOpenapiCannotBeModifyException() {
    super(
        OpenErrorCode.PUBLISHED_OPENAPI_CANNOT_BE_MODIFY,
        Collections.emptyMap(),
        "Published OpenAPI cannot be modified."
    );
  }
}
