package io.github.xezzon.geom.common.exception;

/**
 * 对密钥的操作无效
 * @author xezzon
 */
public class InvalidSecureKeyOperationException extends RuntimeException {

  public InvalidSecureKeyOperationException(String message, Throwable cause) {
    super(message, cause);
  }
}
