package io.github.xezzon.geom.subscription.service;

import io.github.xezzon.geom.core.odata.ODataQueryOption;
import io.github.xezzon.geom.subscription.domain.Subscription;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * @author xezzon
 */
public interface ISubscriptionService4ThirdPartyApp {

  /**
   * 获取订阅列表
   * @param into OData查询选项，用于指定查询条件、排序方式等。
   * @param appId 应用程序ID。
   * @return 包含订阅信息的分页对象。
   */
  Page<Subscription> listSubscription(ODataQueryOption into, String appId);

  /**
   * 获取指定应用ID下订阅的所有OpenAPI列表
   * @param appId 应用ID
   * @return 返回订阅的所有OpenAPI列表
   */
  List<Subscription> listSubscriptionsOfApp(String appId);
}
