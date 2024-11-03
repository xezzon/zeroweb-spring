package io.github.xezzon.geom.common;

import static io.github.xezzon.geom.core.error.ErrorSourceType.CLIENT;

import io.github.xezzon.geom.core.error.ErrorSourceType;
import io.github.xezzon.geom.core.error.IErrorCode;

/**
 * 错误码分配中心
 * @author xezzon
 */
public enum OpenErrorCode implements IErrorCode {

  PUBLISHED_OPENAPI_CANNOT_BE_MODIFY(CLIENT, "已发布的开放接口不能修改"),
  ;

  /**
   * 错误来源类型
   */
  private final ErrorSourceType sourceType;
  /**
   * 错误默认消息
   */
  private final String message;

  OpenErrorCode(ErrorSourceType sourceType, String message) {
    this.sourceType = sourceType;
    this.message = message;
  }

  @Override
  public ErrorSourceType sourceType() {
    return this.sourceType;
  }

  @Override
  public byte moduleCode() {
    return 2;
  }

  @Override
  public String message() {
    return this.message;
  }
}
