package io.github.xezzon.zeroweb.common.exception;

import java.util.Collections;

/**
 * 唯一键冲突
 * @author xezzon
 */
public class RepeatDataException extends ZerowebBusinessException {

  public RepeatDataException(String key) {
    super(
        CommonErrorCode.REPEAT_DATA,
        Collections.singletonMap("keyword", key),
        String.format("`%s` is existed.", key)
    );
  }
}
