package io.github.xezzon.geom.subscription.service;

import io.github.xezzon.geom.subscription.domain.Subscription;
import java.util.Collection;
import java.util.List;

/**
 * @author xezzon
 */
public interface ISubscriptionService4ThirdPartyApp {

  /**
   * 获取指定应用ID下订阅的所有OpenAPI列表
   * @param appId 应用ID
   * @param openapiCodes OpenAPI编码集合
   * @return 返回订阅的所有OpenAPI列表
   */
  List<Subscription> listSubscriptionsOfApp(String appId, Collection<String> openapiCodes);
}
