package io.github.xezzon.zeroweb.common.exception;

/// 文件上传错误
/// @author xezzon
public class WriteFileException extends ZerowebRuntimeException {

  public WriteFileException(Throwable cause) {
    super(cause);
  }

  public WriteFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
