package io.github.xezzon.geom.subscription.service;

import io.github.xezzon.geom.subscription.domain.Subscription;
import java.util.List;

/**
 * @author xezzon
 */
public interface ISubscriptionService4Call {

  /**
   * 获取指定应用ID下订阅的所有OpenAPI列表
   * @param appId 应用ID
   * @return 返回订阅的所有OpenAPI列表
   */
  List<Subscription> listSubscriptionsOfApp(String appId);
}
