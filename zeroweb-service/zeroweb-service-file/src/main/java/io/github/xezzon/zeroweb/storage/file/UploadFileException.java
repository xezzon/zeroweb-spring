package io.github.xezzon.zeroweb.storage.file;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;

/// @author xezzon
public class UploadFileException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "SFC02";

  public UploadFileException(String message) {
    super(message);
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }
}
