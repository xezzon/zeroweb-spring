package io.github.xezzon.zeroweb.common.exception;

import io.github.xezzon.zeroweb.core.error.IErrorCode;

/**
 * @author xezzon
 */
public class DataPermissionForbiddenException extends ZerowebRuntimeException {

  private static final IErrorCode ERROR_CODE = ErrorCode.DATA_PERMISSION_FORBIDDEN;

  public DataPermissionForbiddenException(String message) {
    super(ERROR_CODE, message);
  }
}
