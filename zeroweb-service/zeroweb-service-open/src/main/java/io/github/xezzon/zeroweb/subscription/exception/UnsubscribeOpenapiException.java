package io.github.xezzon.zeroweb.subscription.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jspecify.annotations.NullMarked;

/// 不能调用未订阅的接口
///
/// @author xezzon
@NullMarked
public class UnsubscribeOpenapiException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "CFE04";

  public UnsubscribeOpenapiException() {
    super("Cannot call an unsubscribed OpenAPI.");
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }

  @Override
  public int getHttpStatus() {
    return HttpResponseStatus.FORBIDDEN.code();
  }
}
