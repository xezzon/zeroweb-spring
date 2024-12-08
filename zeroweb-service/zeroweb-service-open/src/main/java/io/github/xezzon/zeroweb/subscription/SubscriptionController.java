package io.github.xezzon.zeroweb.subscription;

import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.subscription.domain.AddSubscriptionReq;
import io.github.xezzon.zeroweb.subscription.domain.Subscription;
import io.github.xezzon.zeroweb.subscription.domain.SubscriptionStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  public SubscriptionController(SubscriptionService subscriptionService) {
    this.subscriptionService = subscriptionService;
  }

  @PostMapping()
  public Id subscribe(@RequestBody AddSubscriptionReq req) {
    Subscription subscription = req.into();
    subscription.setStatus(SubscriptionStatus.AUDITING);
    subscriptionService.addSubscription(subscription);
    return Id.of(subscription.getId());
  }

  @PutMapping("/audit/{id}")
  public void auditSubscription(@PathVariable String id) {
    subscriptionService.auditSubscription(id);
  }
}
