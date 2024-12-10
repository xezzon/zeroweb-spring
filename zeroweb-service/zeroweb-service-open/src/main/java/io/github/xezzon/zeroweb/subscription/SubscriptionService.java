package io.github.xezzon.zeroweb.subscription;

import io.github.xezzon.zeroweb.common.exception.UnpublishedOpenapiCannotBeSubscribeException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.openapi.domain.Openapi;
import io.github.xezzon.zeroweb.openapi.domain.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.service.IOpenapiService4Subscription;
import io.github.xezzon.zeroweb.subscription.domain.Subscription;
import io.github.xezzon.zeroweb.subscription.domain.SubscriptionStatus;
import io.github.xezzon.zeroweb.subscription.service.ISubscriptionService4Call;
import io.github.xezzon.zeroweb.subscription.service.ISubscriptionService4ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.service.IThirdPartyAppService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class SubscriptionService implements
    ISubscriptionService4ThirdPartyApp,
    ISubscriptionService4Call {

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

  protected void auditSubscription(String id) {
    Subscription entity = subscriptionDAO.get().getReferenceById(id);
    if (!Objects.equals(entity.getSubscriptionStatus(), SubscriptionStatus.AUDITING)) {
      // 不是审核中，不变更状态
      return;
    }
    entity.setStatus(SubscriptionStatus.SUBSCRIBED);
    subscriptionDAO.get().save(entity);
  }

  @Override
  public List<Subscription> listSubscriptionsOfApp(String appId) {
    return subscriptionDAO.get().findByAppId(appId);
  }

  @Override
  public Page<Subscription> listSubscription(ODataQueryOption odata, String appId) {
    thirdPartyAppService.checkPermission(appId);
    Page<Openapi> openapiPage = openapiService.listPublishedOpenapi(odata);
    List<Subscription> subscriptions = this.listSubscriptionsOfApp(appId);
    Map<String, Subscription> subscriptionMap = subscriptions.parallelStream()
        .collect(Collectors.toMap(Subscription::getOpenapiCode, s -> s));
    subscriptions = openapiPage.getContent().parallelStream()
        .map(openapi -> {
          Subscription subscription = subscriptionMap.get(openapi.getCode());
          if (subscription == null) {
            subscription = new Subscription();
          }
          subscription.setOpenapi(openapi);
          return subscription;
        })
        .toList();
    return new PageImpl<>(subscriptions, openapiPage.getPageable(), openapiPage.getTotalElements());
  }
}