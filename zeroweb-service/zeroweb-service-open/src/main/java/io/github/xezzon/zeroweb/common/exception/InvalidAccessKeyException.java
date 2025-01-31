package io.github.xezzon.zeroweb.common.exception;

/**
 * 无效的访问密钥
 * 原因：
 * 1. 没传AccessKey或摘要
 * 2. AccessKey或签名使用的SecretKey不正确
 * 3. AccessKey或签名使用的SecretKey不匹配
 * @author xezzon
 */
public class InvalidAccessKeyException extends ZerowebBusinessException {

  public InvalidAccessKeyException() {
    super();
  }
}
