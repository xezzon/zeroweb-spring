package io.github.xezzon.zeroweb.subscription.repository;

import io.github.xezzon.zeroweb.subscription.domain.Subscription;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends
    JpaRepository<Subscription, String>,
    JpaSpecificationExecutor<Subscription> {

  List<Subscription> findByAppIdAndOpenapiCodeIn(String appId, Collection<String> openapiCodes);

  List<Subscription> findByAppId(String appId);
}
