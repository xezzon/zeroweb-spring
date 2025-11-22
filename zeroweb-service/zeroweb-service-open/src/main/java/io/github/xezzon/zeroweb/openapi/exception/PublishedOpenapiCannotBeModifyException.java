package io.github.xezzon.zeroweb.openapi.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import org.jspecify.annotations.NullMarked;

/// @author xezzon
@NullMarked
public class PublishedOpenapiCannotBeModifyException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "CFE01";

  public PublishedOpenapiCannotBeModifyException() {
    super("Published OpenAPI cannot be modified.");
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }
}
