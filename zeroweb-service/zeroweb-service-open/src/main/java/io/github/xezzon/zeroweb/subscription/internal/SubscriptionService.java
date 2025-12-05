/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.xezzon.zeroweb.subscription.internal;

import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.openapi.IOpenapiService4Subscription;
import io.github.xezzon.zeroweb.openapi.Openapi;
import io.github.xezzon.zeroweb.openapi.enumeration.OpenapiStatus;
import io.github.xezzon.zeroweb.subscription.ISubscriptionService4Call;
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

/// 订阅服务实现类，实现了第三方应用接口和调用接口的服务
/// @author xezzon
@Service
public class SubscriptionService implements ISubscriptionService4Call {

  /// 订阅数据仓库，用于数据库操作
  private final SubscriptionRepository subscriptionRepository;
  /// 对外接口服务，用于查询接口信息
  private final IOpenapiService4Subscription openapiService;

  /// 构造器，注入订阅仓库和接口服务
  /// @param subscriptionRepository 订阅数据仓库
  /// @param openapiService 对外接口服务
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
      subscription.setId(exist.getFirst().getId());
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

  /// 获取订阅列表，包含所有已发布接口及指定应用的订阅状态
  /// @param odata OData查询选项，用于指定查询条件、排序方式等
  /// @param appId 第三方应用ID
  /// @return 包含订阅信息的分页对象
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

  /// @param appId 应用ID
  /// @param openapiCode 对外接口编码
  /// @return 订阅信息
  /// @throws UnsubscribeOpenapiException 查询未订阅的接口
  @Override
  public Subscription getSubscription(String appId, String openapiCode) {
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
