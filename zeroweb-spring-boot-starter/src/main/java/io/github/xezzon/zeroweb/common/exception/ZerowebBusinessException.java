package io.github.xezzon.zeroweb.common.exception;

import io.github.xezzon.zeroweb.common.i18n.I18nUtil;
import io.github.xezzon.zeroweb.core.error.IErrorCode;
import java.util.Locale;

/**
 * ZeroWeb 业务异常
 * @author xezzon
 */
public abstract class ZerowebBusinessException extends RuntimeException {

  public static final String EXCEPTION_I18N_BASENAME = "Exception";
  private final transient Object[] args;

  protected ZerowebBusinessException(Object... args) {
    super();
    this.args = args;
  }

  @Override
  public String getMessage() {
    return I18nUtil.formatter(EXCEPTION_I18N_BASENAME)
        .format(getClass().getSimpleName(), super.getMessage(), args);
  }

  public String getLocalizedMessage(Locale locale, String errorName) {
    return I18nUtil.formatter(IErrorCode.I18N_BASENAME)
        .locale(locale)
        .format(errorName, CommonErrorCode.UNKNOWN.name(), args);
  }
}
