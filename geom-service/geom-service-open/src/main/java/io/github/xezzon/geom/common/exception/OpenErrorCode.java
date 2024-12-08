package io.github.xezzon.geom.common.exception;

import static io.github.xezzon.geom.core.error.ErrorSourceType.CLIENT;

import io.github.xezzon.geom.core.error.ErrorSourceType;
import io.github.xezzon.geom.core.error.IErrorCode;

/**
 * 错误码分配中心
 * @author xezzon
 */
public enum OpenErrorCode implements IErrorCode {

  PUBLISHED_OPENAPI_CANNOT_BE_MODIFY(CLIENT, "已发布的开放接口不能修改"),
  UNPUBLISHED_OPENAPI_CANNOT_BE_SUBSCRIBE(CLIENT, "不能订阅未发布的接口"),
  INVALID_ACCESS_KEY(CLIENT, "无效的访问密钥"),
  UNSUBSCRIBED_OPENAPI(CLIENT, "不能调用未订阅的接口"),
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
