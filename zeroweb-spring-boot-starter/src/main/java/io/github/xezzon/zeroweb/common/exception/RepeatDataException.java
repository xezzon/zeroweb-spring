package io.github.xezzon.zeroweb.common.exception;

import java.util.Collections;

/**
 * 唯一键冲突
 * @author xezzon
 */
public class RepeatDataException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "C0006";

  public RepeatDataException(String key) {
    super(
        Collections.singletonMap("keyword", key),
        String.format("`%s` is existed.", key)
    );
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }
}
