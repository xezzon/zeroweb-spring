package io.github.xezzon.zeroweb.common.exception;

import java.util.Collections;

/**
 * @author xezzon
 */
public class DataPermissionForbiddenException extends ZerowebBusinessException {

  public DataPermissionForbiddenException(String message) {
    super(CommonErrorCode.DATA_PERMISSION_FORBIDDEN, Collections.emptyMap(), message);
  }
}
