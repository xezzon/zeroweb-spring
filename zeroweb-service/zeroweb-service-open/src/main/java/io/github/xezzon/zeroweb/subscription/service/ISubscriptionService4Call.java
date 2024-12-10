package io.github.xezzon.zeroweb.subscription.service;

import io.github.xezzon.zeroweb.common.exception.UnsubscribeOpenapiException;
import io.github.xezzon.zeroweb.subscription.domain.Subscription;

/**
 * @author xezzon
 */
public interface ISubscriptionService4Call {

  /**
   * 获取指定应用ID下被订阅的对外接口
   * @param appId 应用ID
   * @param openapiCode 对外接口编码
   * @return 对外接口详情
   * @throws UnsubscribeOpenapiException 不能调用未订阅的对外接口
   */
  Subscription getSubscription(String appId, String openapiCode) throws UnsubscribeOpenapiException;
}
