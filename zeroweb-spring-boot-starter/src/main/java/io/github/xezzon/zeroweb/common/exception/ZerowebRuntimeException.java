package io.github.xezzon.zeroweb.common.exception;

import io.github.xezzon.zeroweb.common.i18n.I18nUtil;
import io.github.xezzon.zeroweb.core.error.IErrorCode;

/**
 * 将受检异常包装成非受检异常
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
        .format(getErrorName(), super.getMessage());
  }

  /**
   * 子类需要以 {@link IErrorCode#name()} 覆盖该方法
   * @return 异常名称，用于后端内容的国际化
   */
  protected String getErrorName() {
    return ZerowebRuntimeException.class.getSimpleName();
  }
}
