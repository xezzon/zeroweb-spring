package io.github.xezzon.zeroweb.auth.exception;

import io.github.xezzon.zeroweb.common.exception.AdminErrorCode;
import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import java.io.Serial;
import java.util.Collections;

/**
 * 不正确的口令
 * @author xezzon
 */
public class InvalidPasswordException extends ZerowebBusinessException {

  @Serial
  private static final long serialVersionUID = 4676151668260963197L;

  public InvalidPasswordException() {
    super(
        AdminErrorCode.INVALID_PASSWORD,
        Collections.emptyMap(),
        "Password is invalid, or user is not existed."
    );
  }
}
