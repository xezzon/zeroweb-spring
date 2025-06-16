package io.github.xezzon.zeroweb.subscription.exception;

import io.github.xezzon.zeroweb.common.exception.OpenErrorCode;
import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import java.util.Collections;

/**
 * @author xezzon
 */
public class UnpublishedOpenapiCannotBeSubscribeException extends ZerowebBusinessException {

  public UnpublishedOpenapiCannotBeSubscribeException() {
    super(
        OpenErrorCode.UNPUBLISHED_OPENAPI_CANNOT_BE_SUBSCRIBE,
        Collections.emptyMap(),
        "Cannot subscribe an unpublished OpenAPI."
    );
  }
}
