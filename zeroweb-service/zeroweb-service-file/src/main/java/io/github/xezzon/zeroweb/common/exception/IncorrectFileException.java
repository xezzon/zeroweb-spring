package io.github.xezzon.zeroweb.common.exception;

import org.jspecify.annotations.NullMarked;

/// 上传文件的内容与创建的附件不一致
/// @author xezzon
@NullMarked
public class IncorrectFileException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "CFC02";

  public IncorrectFileException(String message) {
    super(message);
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }
}
