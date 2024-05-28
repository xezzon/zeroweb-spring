package io.github.xezzon.geom.common.exception;

/**
 * @author xezzon
 */
public enum ErrorCode {

  REPEAT_DATA("A0002", "数据重复"),
  ARGUMENT_NOT_VALID("A0003", "参数错误")
  ;

  private final String code;
  private final String message;

  ErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String code() {
    return this.code;
  }

  public String message() {
    return this.message;
  }
}
