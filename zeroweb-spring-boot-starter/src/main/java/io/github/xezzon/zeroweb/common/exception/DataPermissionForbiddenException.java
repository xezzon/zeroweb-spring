package io.github.xezzon.zeroweb.common.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author xezzon
 */
public class DataPermissionForbiddenException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "C0007";

  public DataPermissionForbiddenException(String message) {
    super(message);
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
