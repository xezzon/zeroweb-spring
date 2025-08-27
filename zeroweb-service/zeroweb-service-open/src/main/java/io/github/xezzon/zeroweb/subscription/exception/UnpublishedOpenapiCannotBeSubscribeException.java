package io.github.xezzon.zeroweb.subscription.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;

/// @author xezzon
public class UnpublishedOpenapiCannotBeSubscribeException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "CFE02";

  public UnpublishedOpenapiCannotBeSubscribeException() {
    super("Cannot subscribe an unpublished OpenAPI.");
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }
}
