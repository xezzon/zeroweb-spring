package io.github.xezzon.geom.subscription;

import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class SubscriptionService {

  private final SubscriptionDAO subscriptionDAO;

  public SubscriptionService(SubscriptionDAO subscriptionDAO) {
    this.subscriptionDAO = subscriptionDAO;
  }
}
