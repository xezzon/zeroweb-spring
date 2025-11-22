package io.github.xezzon.zeroweb.subscription.internal;

import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.openapi.IOpenapiService4Subscription;
import io.github.xezzon.zeroweb.openapi.Openapi;
import io.github.xezzon.zeroweb.openapi.enumeration.OpenapiStatus;
import io.github.xezzon.zeroweb.subscription.ISubscriptionService4Call;
import io.github.xezzon.zeroweb.subscription.ISubscriptionService4ThirdPartyApp;
import io.github.xezzon.zeroweb.subscription.Subscription;
import io.github.xezzon.zeroweb.subscription.enumeration.SubscriptionStatus;
import io.github.xezzon.zeroweb.subscription.exception.UnpublishedOpenapiCannotBeSubscribeException;
import io.github.xezzon.zeroweb.subscription.exception.UnsubscribeOpenapiException;
import io.github.xezzon.zeroweb.subscription.repository.SubscriptionRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

/// @author xezzon
@Service
public class SubscriptionService implements
    ISubscriptionService4ThirdPartyApp,
    ISubscriptionService4Call {

  private final SubscriptionRepository subscriptionRepository;
  private final IOpenapiService4Subscription openapiService;

  public SubscriptionService(
      final SubscriptionRepository subscriptionRepository,
      final IOpenapiService4Subscription openapiService
  ) {
    this.subscriptionRepository = subscriptionRepository;
    this.openapiService = openapiService;
  }

  /// 添加订阅
  ///
  /// 跳过已订阅的接口
  ///
  /// @param subscription 要添加的订阅对象
  /// @throws UnpublishedOpenapiCannotBeSubscribeException 如果要订阅的Openapi未发布，则抛出异常
  protected void addSubscription(Subscription subscription) {
    Openapi openapi = openapiService.getByCode(subscription.getOpenapiCode());
    if (openapi == null || !Objects.equals(openapi.getStatus(), OpenapiStatus.PUBLISHED)) {
      throw new UnpublishedOpenapiCannotBeSubscribeException();
    }
    List<Subscription> exist = subscriptionRepository.findByAppIdAndOpenapiCodeIn(
        subscription.getAppId(),
        Collections.singleton(subscription.getOpenapiCode())
    );
    if (!exist.isEmpty()) {
      // 如果接口已订阅则跳过
      return;
    }
    subscriptionRepository.save(subscription);
  }

  /// 审核订阅。审核后订阅即生效，订阅者可以调用接口。
  ///
  /// 只对审核中的订阅有效。其他状态不变更。
  ///
  /// @param id 订阅的ID
  protected void auditSubscription(String id) {
    Subscription entity = subscriptionRepository.findById(id).orElseThrow();
    if (!Objects.equals(entity.getSubscriptionStatus(), SubscriptionStatus.AUDITING)) {
      // 不是审核中，不变更状态
      return;
    }
    entity.setStatus(SubscriptionStatus.SUBSCRIBED);
    subscriptionRepository.save(entity);
  }

  @Override
  public Page<@NonNull Subscription> listSubscription(ODataQueryOption odata, String appId) {
    Page<@NonNull Openapi> openapiPage = openapiService.listPublishedOpenapi(odata);
    List<Subscription> subscriptions = subscriptionRepository.findByAppId(appId);
    Map<String, Subscription> subscriptionMap = subscriptions.stream()
        .collect(Collectors.toMap(Subscription::getOpenapiCode, s -> s));
    subscriptions = openapiPage.getContent().stream()
        .map(openapi -> {
          Subscription subscription = subscriptionMap
              .computeIfAbsent(openapi.getCode(), _ -> new Subscription());
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
    List<Subscription> subscriptions = subscriptionRepository
        .findByAppIdAndOpenapiCodeIn(appId, Collections.singleton(openapiCode));
    if (subscriptions.isEmpty()) {
      throw new UnsubscribeOpenapiException();
    }
    Subscription subscription = subscriptions.getFirst();
    if (subscription.getSubscriptionStatus() != SubscriptionStatus.SUBSCRIBED) {
      throw new UnsubscribeOpenapiException();
    }
    Openapi openapi = openapiService.getByCode(subscription.getOpenapiCode());
    subscription.setOpenapi(openapi);
    return subscription;
  }
}
