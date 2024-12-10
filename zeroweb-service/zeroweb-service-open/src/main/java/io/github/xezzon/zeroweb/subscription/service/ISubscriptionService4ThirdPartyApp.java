package io.github.xezzon.zeroweb.subscription.service;

import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.subscription.domain.Subscription;
import org.springframework.data.domain.Page;

/**
 * @author xezzon
 */
public interface ISubscriptionService4ThirdPartyApp {

  /**
   * 获取订阅列表
   * @param odata OData查询选项，用于指定查询条件、排序方式等。
   * @param appId 应用程序ID。
   * @return 包含订阅信息的分页对象。
   * @throws DataPermissionForbiddenException 只有应用所有者有权限访问
   */
  Page<Subscription> listSubscription(ODataQueryOption odata, String appId)
      throws DataPermissionForbiddenException;
}
