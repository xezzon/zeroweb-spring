package io.github.xezzon.zeroweb.common.exception;

import static io.github.xezzon.zeroweb.core.error.ErrorSourceType.AUTHORIZATION;
import static io.github.xezzon.zeroweb.core.error.ErrorSourceType.CLIENT;
import static io.github.xezzon.zeroweb.core.error.ErrorSourceType.SERVER;

import io.github.xezzon.zeroweb.core.error.ErrorSourceType;
import io.github.xezzon.zeroweb.core.error.IErrorCode;

/**
 * 错误码分配中心
 * @author xezzon
 */
public enum CommonErrorCode implements IErrorCode {

  UNKNOWN(SERVER),
  /**
   * 唯一键冲突
   * 消息参数0: 重复的键名
   */
  REPEAT_DATA(CLIENT),
  /**
   * HTTP请求参数不符合校验规则
   */
  ARGUMENT_NOT_VALID(CLIENT),
  /**
   * 数据不存在或已删除
   */
  NO_SUCH_DATA(CLIENT),
  /**
   * 资源不存在
   */
  NOT_FOUND(CLIENT),
  /**
   * 未登录
   */
  NOT_LOGIN(CLIENT),
  /**
   * 无效的token
   */
  INVALID_TOKEN(SERVER),
  /**
   * 数据权限无效
   */
  DATA_PERMISSION_FORBIDDEN(AUTHORIZATION),
  /**
   * 用户没有指定的权限
   */
  NOT_PERMISSION(CLIENT),
  ;

  /**
   * 错误来源类型
   */
  private final ErrorSourceType sourceType;

  CommonErrorCode(ErrorSourceType sourceType) {
    this.sourceType = sourceType;
  }

  @Override
  public ErrorSourceType sourceType() {
    return this.sourceType;
  }

  @Override
  public byte moduleCode() {
    return 0;
  }
}
