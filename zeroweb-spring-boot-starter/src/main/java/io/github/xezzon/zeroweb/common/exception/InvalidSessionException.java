package io.github.xezzon.zeroweb.common.exception;

/**
 * 无效的会话信息
 * @author xezzon
 */
public class InvalidSessionException extends ZerowebRuntimeException {

  public InvalidSessionException(Throwable cause) {
    super("Invalid session", cause);
  }
}
