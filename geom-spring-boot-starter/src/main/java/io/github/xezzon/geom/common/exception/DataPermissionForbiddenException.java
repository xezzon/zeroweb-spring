package io.github.xezzon.geom.common.exception;

import io.github.xezzon.geom.core.error.IErrorCode;

/**
 * @author xezzon
 */
public class DataPermissionForbiddenException extends GeomRuntimeException {

  private static final IErrorCode ERROR_CODE = ErrorCode.DATA_PERMISSION_FORBIDDEN;

  public DataPermissionForbiddenException(String message) {
    super(ERROR_CODE, message);
  }
}
