package io.github.xezzon.geom.subscription.repository;

import io.github.xezzon.geom.subscription.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends
    JpaRepository<Subscription, String>,
    JpaSpecificationExecutor<Subscription> {

}
