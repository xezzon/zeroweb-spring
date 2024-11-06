package io.github.xezzon.geom.common.exception;

import java.io.Serial;

/**
 * 不正确的口令
 * @author xezzon
 */
public class InvalidTokenException extends GeomRuntimeException {

  @Serial
  private static final long serialVersionUID = 4676151668260963197L;

  public InvalidTokenException() {
    super(AdminErrorCode.INVALID_TOKEN);
  }
}
