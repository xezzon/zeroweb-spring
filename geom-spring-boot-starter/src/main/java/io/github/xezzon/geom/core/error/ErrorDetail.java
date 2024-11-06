package io.github.xezzon.geom.core.error;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 * API异常响应对象，与 Error-Code 响应头对应
 * @author xezzon
 */
@Getter
public class ErrorDetail {

  /**
   * 服务端定义的一组错误码
   */
  private final String code;
  /**
   * 错误的可读表述
   */
  private final String message;
  /**
   * 有关导致该报告错误的具体错误的详细信息数组
   */
  private final List<ErrorDetail> details;

  public ErrorDetail(String code, String message, List<ErrorDetail> details) {
    this.code = code;
    this.message = message;
    this.details = details;
  }

  public ErrorDetail(String code, String message) {
    this(code, message, Collections.emptyList());
  }
}
