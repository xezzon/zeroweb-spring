package io.github.xezzon.zeroweb.common.exception;

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
}
