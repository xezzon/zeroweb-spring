package io.github.xezzon.zeroweb.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

/**
 * API异常响应对象，与 Error-Code 响应头对应
 * @author xezzon
 */
@Getter
public class ErrorResult {

  /**
   * 服务端定义的一组错误码
   */
  private String code;
  /**
   * 错误的可读表述
   */
  private String message;
  /**
   * 用于消息插值的参数
   */
  private Map<String, Object> parameters = Collections.emptyMap();
  /**
   * 有关导致该报告错误的具体错误的详细信息数组
   */
  @JsonInclude(Include.NON_NULL)
  private List<Detail> details;

  public ErrorResult(Throwable e) {
    this.code = getCode(e);
    this.message = e.getLocalizedMessage();
    if (e instanceof ZerowebBusinessException zbe) {
      this.parameters = Optional.ofNullable(zbe.getParameters()).orElseGet(Collections::emptyMap);
    }
  }

  public ErrorResult(Throwable e, List<Detail> details) {
    this(e);
    this.details = details;
  }

  protected ErrorResult(String code, String message, Map<String, Object> parameters) {
    this.code = code;
    this.message = message;
    this.parameters = parameters;
  }

  @SuppressWarnings("unused")
  ErrorResult() {
  }

  public static String getCode(Throwable e) {
    String name = e.getClass().getSimpleName();
    final String suffix = "Exception";
    return name.endsWith(suffix) ? name.substring(0, name.length() - suffix.length()) : name;
  }

  public static class Detail extends ErrorResult {

    public Detail(String code, String message, Map<String, Object> parameters) {
      super(code, message, parameters);
    }
  }
}
