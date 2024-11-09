package io.github.xezzon.geom.common.exception;

import static io.github.xezzon.geom.core.error.ErrorSourceType.CLIENT;
import static io.github.xezzon.geom.core.error.ErrorSourceType.SERVER;

import io.github.xezzon.geom.core.error.ErrorSourceType;
import io.github.xezzon.geom.core.error.IErrorCode;

/**
 * 错误码分配中心
 * @author xezzon
 */
public enum ErrorCode implements IErrorCode {

  UNKNOWN(SERVER, "未知错误"),
  /**
   * 唯一键冲突
   */
  REPEAT_DATA(CLIENT, "数据重复"),
  /**
   * HTTP请求参数不符合校验规则
   */
  ARGUMENT_NOT_VALID(CLIENT, "参数错误"),
  /**
   * 数据不存在或已删除
   */
  NO_SUCH_DATA(CLIENT, "数据不存在或已删除"),
  /**
   * 资源不存在
   */
  NOT_FOUND(CLIENT, "资源不存在"),
  /**
   * 未登录
   */
  NOT_LOGIN(CLIENT, "未登录"),
  /**
   * 无效的token
   */
  INVALID_TOKEN(SERVER, "无效的token"),
  ;

  /**
   * 错误码
   */
  private final ErrorSourceType sourceType;
  /**
   * 错误默认消息
   */
  private final String message;

  ErrorCode(ErrorSourceType sourceType, String message) {
    this.sourceType = sourceType;
    this.message = message;
  }

  @Override
  public ErrorSourceType sourceType() {
    return this.sourceType;
  }

  @Override
  public byte moduleCode() {
    return 0;
  }

  /**
   * @return 错误默认消息
   */
  public String message() {
    return this.message;
  }
}
