package io.github.xezzon.zeroweb.common.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author xezzon
 */
public class ErrorCodeConstant {

  public static final int CLIENT_ERROR_STATUS = HttpResponseStatus.UNPROCESSABLE_ENTITY.code();
  public static final int SERVER_ERROR_STATUS = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
  public static final String UNKNOWN = "S0001";
  public static final String UNAUTHENTICATED = "C0002";
  public static final String UNAUTHORIZED = "C0003";
  public static final String ARGUMENT_INVALID = "C0005";
  public static final String NO_SUCH_DATA = "C0008";
  /**
   * 错误码的请求头名称
   */
  public static final String ERROR_CODE_HEADER = "X-Error-Code";

  private ErrorCodeConstant() {
  }
}
