package io.github.xezzon.zeroweb.subscription.internal;

import static io.github.xezzon.zeroweb.subscription.authz.SubscriptionPermissionConstant.LIST_SUBSCRIPTION;
import static io.github.xezzon.zeroweb.subscription.authz.SubscriptionPermissionConstant.SUBSCRIBE;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.core.odata.ODataRequestParam;
import io.github.xezzon.zeroweb.subscription.Subscription;
import io.github.xezzon.zeroweb.subscription.authz.SubscriptionPermissionManager;
import io.github.xezzon.zeroweb.subscription.entity.AddSubscriptionReq;
import io.github.xezzon.zeroweb.subscription.enumeration.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/// 接口订阅管理
///
/// @author xezzon
@RestController
public class SubscriptionHttpEndpoint {

  private final SubscriptionService subscriptionService;
  private final SubscriptionPermissionManager subscriptionPermissionManager;

  public SubscriptionHttpEndpoint(
      final SubscriptionService subscriptionService,
      final SubscriptionPermissionManager subscriptionPermissionManager
  ) {
    this.subscriptionService = subscriptionService;
    this.subscriptionPermissionManager = subscriptionPermissionManager;
  }

  /// 查询所有已发布的对外接口以及指定第三方应用的订阅情况
  ///
  /// @param odata 查询参数
  /// @param appId 第三方应用ID
  /// @return 所有已发布的对外接口以及指定第三方应用的订阅情况
  @GetMapping("/third-party-app/{appId}/subscription")
  public Page<Subscription> listSubscription(ODataRequestParam odata, @PathVariable String appId) {
    if (!StpUtil.hasPermission(PermissionConstant.SUBSCRIPTION_AUDIT)) {
      // 应用管理员可以查看所有应用的订阅，非管理员则需要对应的权限
      subscriptionPermissionManager.check(appId, JwtAuth.getOrThrow().getSub(), LIST_SUBSCRIPTION);
    }
    return subscriptionService.listSubscription(odata.into(), appId);
  }

  /// 订阅对外接口
  ///
  /// @param req 接口订阅信息
  /// @return 订阅标识
  @PostMapping("/subscription")
  public Id subscribe(@RequestBody AddSubscriptionReq req) {
    subscriptionPermissionManager.check(req.appId(), JwtAuth.getOrThrow().getSub(), SUBSCRIBE);
    Subscription subscription = req.into();
    subscription.setStatus(SubscriptionStatus.AUDITING);
    subscriptionService.addSubscription(subscription);
    return Id.of(subscription.getId());
  }

  /// 审核订阅
  ///
  /// 审核后第三方应用即可调用该接口
  ///
  /// @param id 订阅标识
  @PutMapping("/subscription/audit/{id}")
  @SaCheckPermission({PermissionConstant.SUBSCRIPTION_AUDIT})
  public void auditSubscription(@PathVariable String id) {
    subscriptionService.auditSubscription(id);
  }
}
