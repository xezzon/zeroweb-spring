package io.github.xezzon.geom.subscription;

import io.github.xezzon.geom.common.UnpublishedOpenapiCannotBeSubscribeException;
import io.github.xezzon.geom.openapi.domain.Openapi;
import io.github.xezzon.geom.openapi.domain.OpenapiStatus;
import io.github.xezzon.geom.openapi.service.IOpenapiService4Subscription;
import io.github.xezzon.geom.subscription.domain.Subscription;
import io.github.xezzon.geom.subscription.service.ISubscriptionService4ThirdPartyApp;
import io.github.xezzon.geom.third_party_app.service.IThirdPartyAppService;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class SubscriptionService implements ISubscriptionService4ThirdPartyApp {

  private final SubscriptionDAO subscriptionDAO;
  private final IThirdPartyAppService thirdPartyAppService;
  private final IOpenapiService4Subscription openapiService;

  public SubscriptionService(
      SubscriptionDAO subscriptionDAO,
      IThirdPartyAppService thirdPartyAppService,
      IOpenapiService4Subscription openapiService
  ) {
    this.subscriptionDAO = subscriptionDAO;
    this.thirdPartyAppService = thirdPartyAppService;
    this.openapiService = openapiService;
  }

  protected void addSubscription(Subscription subscription) {
    thirdPartyAppService.checkPermission(subscription.getAppId());
    Openapi openapi = openapiService.getByCode(subscription.getOpenapiCode());
    if (openapi == null || !Objects.equals(openapi.getStatus(), OpenapiStatus.PUBLISHED)) {
      throw new UnpublishedOpenapiCannotBeSubscribeException();
    }
    List<Subscription> exist = subscriptionDAO.get().findByAppIdAndOpenapiCodeIn(
        subscription.getAppId(),
        Collections.singleton(subscription.getOpenapiCode())
    );
    if (!exist.isEmpty()) {
      // 如果接口已订阅则跳过
      return;
    }
    subscriptionDAO.get().save(subscription);
  }

  @Override
  public List<Subscription> listSubscriptionsOfApp(String appId, Collection<String> openapiCodes) {
    return subscriptionDAO.get().findByAppIdAndOpenapiCodeIn(appId, openapiCodes);
  }
}
