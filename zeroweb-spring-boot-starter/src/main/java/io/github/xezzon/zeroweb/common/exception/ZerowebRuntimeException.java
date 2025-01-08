package io.github.xezzon.zeroweb.common.exception;

import io.github.xezzon.zeroweb.common.i18n.I18nUtil;
import io.github.xezzon.zeroweb.core.error.IErrorCode;

/**
 * ZeroWeb 自发抛出的系统异常
 * @author xezzon
 */
public class ZerowebRuntimeException extends RuntimeException {

  public ZerowebRuntimeException() {
    super();
  }

  public ZerowebRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public ZerowebRuntimeException(Throwable cause) {
    super(cause);
  }

  @Override
  public String getMessage() {
    return I18nUtil.formatter(IErrorCode.I18N_BASENAME)
        .format(getClass().getSimpleName(), super.getMessage());
  }
}
