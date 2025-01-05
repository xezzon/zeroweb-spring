package io.github.xezzon.zeroweb.common.exception;

/**
 * 唯一键冲突
 * @author xezzon
 */
public class RepeatDataException extends ZerowebBusinessException {

  public RepeatDataException(String key) {
    super(key);
  }
}
