package io.github.xezzon.zeroweb.third_party_app.exception;

import io.github.xezzon.zeroweb.common.exception.ZerowebBusinessException;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 无效的访问密钥
 * 原因：
 * 1. 没传AccessKey或摘要
 * 2. AccessKey或签名使用的SecretKey不正确
 * 3. AccessKey或签名使用的SecretKey不匹配
 * @author xezzon
 */
public class InvalidAccessKeyException extends ZerowebBusinessException {

  public static final String ERROR_CODE = "CFE03";

  public InvalidAccessKeyException() {
    super("An Invalid Access Key");
  }

  @Override
  public String getCode() {
    return ERROR_CODE;
  }

  @Override
  public int getHttpStatus() {
    return HttpResponseStatus.FORBIDDEN.code();
  }
}
