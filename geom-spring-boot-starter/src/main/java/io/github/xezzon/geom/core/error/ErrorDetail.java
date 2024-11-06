package io.github.xezzon.geom.core.error;

import java.util.Collections;
import java.util.List;

/**
 * API异常响应对象，与 Error-Code 响应头对应
 * @param code 服务端定义的一组错误码
 * @param message 错误的可读表述
 * @param details 有关导致该报告错误的具体错误的详细信息数组
 * @author xezzon
 */
public record ErrorDetail(
    String code,
    String message,
    List<ErrorDetail> details
) {

  public ErrorDetail(String code, String message) {
    this(code, message, Collections.emptyList());
  }
}
