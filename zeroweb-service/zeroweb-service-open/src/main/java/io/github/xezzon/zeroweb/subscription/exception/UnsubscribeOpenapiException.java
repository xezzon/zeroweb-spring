package io.github.xezzon.zeroweb.subscription.exception;

import io.github.xezzon.zeroweb.common.exception.OpenErrorCode;
import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import java.util.Collections;

/**
 * 不能调用未订阅的接口
 * @author xezzon
 */
public class UnsubscribeOpenapiException extends ZerowebBusinessException {

  public UnsubscribeOpenapiException() {
    super(
        OpenErrorCode.UNSUBSCRIBED_OPENAPI,
        Collections.emptyMap(),
        "Cannot call an unsubscribed OpenAPI."
    );
  }
}
