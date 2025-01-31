package io.github.xezzon.zeroweb.subscription;

import io.github.xezzon.zeroweb.common.exception.UnpublishedOpenapiCannotBeSubscribeException;
import io.github.xezzon.zeroweb.common.exception.UnsubscribeOpenapiException;
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

  /**
   * 添加订阅
   * 跳过已订阅的接口
   * @param subscription 要添加的订阅对象
   * @throws UnpublishedOpenapiCannotBeSubscribeException 如果要订阅的Openapi未发布，则抛出异常
   */
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

  /**
   * 审核订阅。审核后订阅即生效，订阅者可以调用接口。
   * 只对审核中的订阅有效。其他状态不变更。
   * @param id 订阅的ID
   */
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
  public Page<Subscription> listSubscription(ODataQueryOption odata, String appId) {
    thirdPartyAppService.checkPermission(appId);
    Page<Openapi> openapiPage = openapiService.listPublishedOpenapi(odata);
    List<Subscription> subscriptions = subscriptionDAO.get().findByAppId(appId);
    Map<String, Subscription> subscriptionMap = subscriptions.parallelStream()
        .collect(Collectors.toMap(Subscription::getOpenapiCode, s -> s));
    subscriptions = openapiPage.getContent().parallelStream()
        .map(openapi -> {
          Subscription subscription = subscriptionMap.get(openapi.getCode());
          if (subscription == null) {
            subscription = new Subscription();
          }
          subscription.setOpenapi(openapi);
          openapi.setDestination(null);  // 内部路径不允许暴露给订阅者
          return subscription;
        })
        .toList();
    return new PageImpl<>(subscriptions, openapiPage.getPageable(), openapiPage.getTotalElements());
  }

  @Override
  public Subscription getSubscription(String appId, String openapiCode)
      throws UnsubscribeOpenapiException {
    List<Subscription> subscriptions = subscriptionDAO.get()
        .findByAppIdAndOpenapiCodeIn(appId, Collections.singleton(openapiCode));
    if (subscriptions.isEmpty()) {
      throw new UnsubscribeOpenapiException();
    }
    Subscription subscription = subscriptions.get(0);
    if (subscription.getSubscriptionStatus() != SubscriptionStatus.SUBSCRIBED) {
      throw new UnsubscribeOpenapiException();
    }
    Openapi openapi = openapiService.getByCode(subscription.getOpenapiCode());
    subscription.setOpenapi(openapi);
    return subscription;
  }
}
