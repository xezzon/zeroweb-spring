package io.github.xezzon.zeroweb.common.exception;

import static io.github.xezzon.zeroweb.core.error.ErrorSourceType.AUTHORIZATION;
import static io.github.xezzon.zeroweb.core.error.ErrorSourceType.CLIENT;

import io.github.xezzon.zeroweb.core.error.ErrorSourceType;
import io.github.xezzon.zeroweb.core.error.IErrorCode;

/**
 * 错误码分配中心
 * @author xezzon
 */
public enum OpenErrorCode implements IErrorCode {

  PUBLISHED_OPENAPI_CANNOT_BE_MODIFY(CLIENT),
  UNPUBLISHED_OPENAPI_CANNOT_BE_SUBSCRIBE(CLIENT),
  INVALID_ACCESS_KEY(AUTHORIZATION),
  UNSUBSCRIBED_OPENAPI(AUTHORIZATION),
  ;

  /**
   * 错误来源类型
   */
  private final ErrorSourceType sourceType;

  OpenErrorCode(ErrorSourceType sourceType) {
    this.sourceType = sourceType;
  }

  @Override
  public ErrorSourceType sourceType() {
    return this.sourceType;
  }

  @Override
  public byte moduleCode() {
    return -2;
  }
}
