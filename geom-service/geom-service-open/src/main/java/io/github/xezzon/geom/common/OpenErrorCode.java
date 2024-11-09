package io.github.xezzon.geom.common;

/**
 * 错误码分配中心
 * @author xezzon
 */
public enum OpenErrorCode {

  PUBLISHED_OPENAPI_CANNOT_BE_MODIFY("A1011", "已发布的开放接口不能修改"),
  ;

  /**
   * 错误码
   */
  private final String code;
  /**
   * 错误默认消息
   */
  private final String message;

  OpenErrorCode(String code, String message) {
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
