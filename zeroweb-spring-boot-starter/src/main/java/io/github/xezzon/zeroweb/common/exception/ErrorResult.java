package io.github.xezzon.zeroweb.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * API异常响应对象，与 Error-Code 响应头对应
 * @author xezzon
 */
@Getter
public class ErrorResult {

  /**
   * 服务端定义的一组错误码
   */
  private final String code;
  /**
   * 错误的可读表述
   */
  private final String message;
  /**
   * 用于消息插值的参数
   */
  private Map<String, Object> parameters = Collections.emptyMap();
  /**
   * 有关导致该报告错误的具体错误的详细信息数组
   */
  @JsonInclude(Include.NON_NULL)
  private List<ErrorResult> details;

  public ErrorResult(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public ErrorResult setParameters(@NotNull Map<String, Object> parameters) {
    this.parameters = parameters;
    return this;
  }

  public ErrorResult setDetails(@NotNull List<ErrorResult> details) {
    this.details = details;
    return this;
  }
}
