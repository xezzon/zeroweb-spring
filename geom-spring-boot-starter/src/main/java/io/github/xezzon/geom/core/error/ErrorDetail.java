package io.github.xezzon.geom.core.error;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * API异常响应对象，与 Error-Code 响应头对应
 * @author xezzon
 */
@Getter
public class ErrorDetail {

  /**
   * 服务端定义的一组错误码
   */
  private String code;
  /**
   * 错误的可读表述
   */
  private String message;
  /**
   * 有关导致该报告错误的具体错误的详细信息数组
   */
  private List<ErrorDetail> details;

  ErrorDetail() {
    this.details = Collections.emptyList();
  }

  public ErrorDetail(String code, String message) {
    this();
    this.code = code;
    this.message = message;
  }

  public ErrorDetail setDetails(@NotNull List<ErrorDetail> details) {
    this.details = details;
    return this;
  }
}
