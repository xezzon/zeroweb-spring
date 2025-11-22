package io.github.xezzon.zeroweb.common.exception;

import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import org.jspecify.annotations.NullMarked;

/// 启用的存储后端未正确配置
/// @author xezzon
@NullMarked
public class UnsupportedFileProviderException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "SFC01";

  public UnsupportedFileProviderException(FileProviderEnum provider) {
    super(provider + " is not configured correctly.");
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }

  @Override
  public int getHttpStatus() {
    return ErrorCodeConstant.SERVER_ERROR_STATUS;
  }
}
