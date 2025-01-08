package io.github.xezzon.zeroweb.common.exception;

import java.io.Serial;

/**
 * 不正确的口令
 * @author xezzon
 */
public class InvalidTokenException extends ZerowebRuntimeException {

  @Serial
  private static final long serialVersionUID = 4676151668260963197L;

  public InvalidTokenException(Throwable e) {
    super(e);
  }
}
