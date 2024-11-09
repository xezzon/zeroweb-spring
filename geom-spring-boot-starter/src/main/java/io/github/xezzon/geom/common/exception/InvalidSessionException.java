package io.github.xezzon.geom.common.exception;

/**
 * 无效的会话信息
 * @author xezzon
 */
public class InvalidSessionException extends RuntimeException {

  public InvalidSessionException(Throwable cause) {
    super("Invalid session", cause);
  }
}
