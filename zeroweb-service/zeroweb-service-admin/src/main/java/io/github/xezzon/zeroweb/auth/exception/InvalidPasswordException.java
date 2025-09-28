package io.github.xezzon.zeroweb.auth.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;

/// 不正确的口令
///
/// @author xezzon
public class InvalidPasswordException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "CFF01";

  public InvalidPasswordException() {
    super("Password is invalid, or user is not existed.");
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }
}
