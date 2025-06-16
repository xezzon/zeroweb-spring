package io.github.xezzon.zeroweb.common.exception;

import static io.github.xezzon.zeroweb.core.error.ErrorSourceType.CLIENT;

import io.github.xezzon.zeroweb.core.error.ErrorSourceType;
import io.github.xezzon.zeroweb.core.error.IErrorCode;

/**
 * 错误码分配中心
 * @author xezzon
 */
public enum AdminErrorCode implements IErrorCode {

  /**
   * 用户名或密码错误
   */
  INVALID_PASSWORD(CLIENT),
  /**
   * 角色不能继承
   */
  ROLE_NOT_INHERITABLE(CLIENT),
  ;

  /**
   * 错误来源类型
   */
  private final ErrorSourceType sourceType;

  AdminErrorCode(ErrorSourceType sourceType) {
    this.sourceType = sourceType;
  }

  @Override
  public ErrorSourceType sourceType() {
    return this.sourceType;
  }

  @Override
  public byte moduleCode() {
    return -1;
  }
}
