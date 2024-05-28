package io.github.xezzon.geom.common.exception;

import io.github.xezzon.tao.exception.ClientException;

/**
 * @author xezzon
 */
public class RepeatDataException extends ClientException {

  public RepeatDataException(String message) {
    super(ErrorCode.REPEAT_DATA.code(), message);
  }
}
