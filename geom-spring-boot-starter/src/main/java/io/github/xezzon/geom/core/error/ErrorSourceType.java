package io.github.xezzon.geom.core.error;

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
  NONE("0"),
  /**
   * 客户端异常
   * 需要通知给调用方处理
   */
  CLIENT("C"),
  /**
   * 服务端异常
   * 通常由代码、配置等错误引起，需要通知开发人员处理
   */
  SERVER("S"),
  /**
   * 第三方服务异常
   * 通常是调用第三方服务异常，需要通知相关运维人员处理
   */
  THIRD_PARTY("T"),
  ;

  private final String code;

  ErrorSourceType(String code) {
    this.code = code;
  }
}
