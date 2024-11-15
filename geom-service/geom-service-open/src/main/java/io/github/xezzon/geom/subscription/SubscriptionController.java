package io.github.xezzon.geom.subscription;

import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.subscription.domain.AddSubscriptionReq;
import io.github.xezzon.geom.subscription.domain.Subscription;
import io.github.xezzon.geom.subscription.domain.SubscriptionStatus;
import org.springframework.web.bind.annotation.PostMapping;
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
}
