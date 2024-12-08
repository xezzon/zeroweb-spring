package io.github.xezzon.zeroweb.core.error;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;

/**
 * 错误来源类型
 * @author xezzon
 */
@Getter
public enum ErrorSourceType {

  /**
   * 非异常占位符
   */
  NONE("0", OK),
  /**
   * 客户端异常
   * 需要通知给调用方处理
   */
  CLIENT("C", BAD_REQUEST),
  /**
   * 服务端异常
   * 通常由代码、配置等错误引起，需要通知开发人员处理
   */
  SERVER("S", INTERNAL_SERVER_ERROR),
  /**
   * 第三方服务异常
   * 通常是调用第三方服务异常，需要通知相关运维人员处理
   */
  THIRD_PARTY("T", INTERNAL_SERVER_ERROR),
  ;

  private final String code;
  private final int responseCode;

  ErrorSourceType(String code, HttpResponseStatus responseStatus) {
    this.responseCode = responseStatus.code();
    this.code = code;
  }
}
