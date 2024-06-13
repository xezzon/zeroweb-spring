package io.github.xezzon.geom.common.exception;

/**
 * 错误码分配中心
 * @author xezzon
 */
public enum ErrorCode {

  /**
   * 唯一键冲突
   */
  REPEAT_DATA("A0002", "数据重复"),
  /**
   * HTTP请求参数不符合校验规则
   */
  ARGUMENT_NOT_VALID("A0003", "参数错误"),
  /**
   * 用户名或密码错误
   */
  INVALID_TOKEN("A0101", "用户名或密码错误"),
  ;

  /**
   * 错误码
   */
  private final String code;
  /**
   * 错误默认消息
   */
  private final String message;

  ErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  /**
   * @return 错误码
   */
  public String code() {
    return this.code;
  }

  /**
   * @return 错误默认消息
   */
  public String message() {
    return this.message;
  }
}
