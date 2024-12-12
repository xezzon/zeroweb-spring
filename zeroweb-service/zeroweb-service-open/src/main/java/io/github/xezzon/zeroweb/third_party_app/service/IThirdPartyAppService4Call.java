package io.github.xezzon.zeroweb.third_party_app.service;

import io.github.xezzon.zeroweb.common.exception.InvalidAccessKeyException;
import java.time.Instant;

public interface IThirdPartyAppService4Call {

  /**
   * 校验摘要，如果校验成功则签发JWT，否则抛出异常。
   * @param accessKey AccessKey
   * @param body 消息内容
   * @param signature 消息摘要
   * @param iat 消息签发时间
   * @return 携带认证信息的JWT
   * @throws InvalidAccessKeyException 如果摘要校验失败则抛出此异常
   */
  String signJwt(String accessKey, byte[] body, String signature, Instant iat)
      throws InvalidAccessKeyException;
}
