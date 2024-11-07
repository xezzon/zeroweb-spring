package io.github.xezzon.geom.common.exception;

/**
 * 唯一键冲突
 * @author xezzon
 */
public class RepeatDataException extends GeomRuntimeException {

  /**
   * 初始化异常
   * @param message 异常消息
   */
  public RepeatDataException(String message) {
    super(ErrorCode.REPEAT_DATA);
  }
}
