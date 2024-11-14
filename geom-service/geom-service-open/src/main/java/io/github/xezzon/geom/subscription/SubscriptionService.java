package io.github.xezzon.geom.subscription;

import io.github.xezzon.geom.subscription.domain.Subscription;
import io.github.xezzon.geom.subscription.repository.SubscriptionRepository;
import io.github.xezzon.geom.subscription.service.ISubscriptionService4ThirdPartyApp;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class SubscriptionService implements ISubscriptionService4ThirdPartyApp {

  private final SubscriptionDAO subscriptionDAO;

  public SubscriptionService(SubscriptionDAO subscriptionDAO) {
    this.subscriptionDAO = subscriptionDAO;
  }

  @Override
  public List<Subscription> listSubscriptionsOfApp(String appId, Collection<String> openapiCodes) {
    return subscriptionDAO.get().findByAppIdAndOpenapiCodeIn(appId, openapiCodes);
  }
}
