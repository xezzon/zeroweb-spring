package io.github.xezzon.zeroweb.subscription;

import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.subscription.domain.Subscription;
import io.github.xezzon.zeroweb.subscription.domain.SubscriptionStatus;
import io.github.xezzon.zeroweb.subscription.entity.AddSubscriptionReq;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口订阅管理
 * @author xezzon
 */
@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  public SubscriptionController(SubscriptionService subscriptionService) {
    this.subscriptionService = subscriptionService;
  }

  /**
   * 订阅对外接口
   * @param req 接口订阅信息
   * @return 订阅标识
   */
  @PostMapping()
  public Id subscribe(@RequestBody AddSubscriptionReq req) {
    Subscription subscription = req.into();
    subscription.setStatus(SubscriptionStatus.AUDITING);
    subscriptionService.addSubscription(subscription);
    return Id.of(subscription.getId());
  }

  /**
   * 审核订阅
   * 审核后第三方应用即可调用该接口
   * @param id 订阅标识
   */
  @PutMapping("/audit/{id}")
  public void auditSubscription(@PathVariable String id) {
    subscriptionService.auditSubscription(id);
  }
}
