package io.github.xezzon.zeroweb.third_party_app.service;

import io.github.xezzon.zeroweb.common.exception.InvalidAccessKeyException;
import io.github.xezzon.zeroweb.third_party_app.domain.ThirdPartyApp;

/**
 * @author xezzon
 */
public interface IThirdPartyAppService {

  /**
   * 检查当前人员是否有指定应用的权限
   * @param appId 应用ID
   */
  void checkPermission(String appId);

  /**
   * 校验摘要
   * @param appId 应用标识
   * @param body 消息体
   * @param signature 摘要
   * @throws InvalidAccessKeyException 签名校验失败
   */
  void validateSignature(String appId, byte[] body, String signature);

  /**
   * 根据第三方应用标识查找第三方应用
   * @param appId 第三方应用标识
   * @return 返回与给定第三方应用标识相对应的第三方应用对象
   */
  ThirdPartyApp findById(String appId);
}
