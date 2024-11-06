package io.github.xezzon.geom.common.exception;

import static io.github.xezzon.geom.core.error.ErrorSourceType.CLIENT;

import io.github.xezzon.geom.core.error.ErrorSourceType;
import io.github.xezzon.geom.core.error.IErrorCode;

/**
 * 错误码分配中心
 * @author xezzon
 */
public enum AdminErrorCode implements IErrorCode {

  /**
   * 用户名或密码错误
   */
  INVALID_TOKEN(CLIENT, "用户名或密码错误"),
  ;

  /**
   * 错误码
   */
  private final ErrorSourceType sourceType;
  /**
   * 错误默认消息
   */
  private final String message;

  AdminErrorCode(ErrorSourceType sourceType, String message) {
    this.sourceType = sourceType;
    this.message = message;
  }

  @Override
  public ErrorSourceType sourceType() {
    return this.sourceType;
  }

  @Override
  public byte moduleCode() {
    return 1;
  }

  @Override
  public String message() {
    return this.message;
  }
}
