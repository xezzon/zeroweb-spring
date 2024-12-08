package io.github.xezzon.zeroweb.subscription;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.subscription.domain.Subscription;
import io.github.xezzon.zeroweb.subscription.repository.SubscriptionRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class SubscriptionDAO extends BaseDAO<Subscription, String, SubscriptionRepository> {

  protected SubscriptionDAO(SubscriptionRepository repository) {
    super(repository, Subscription.class);
  }

  @Override
  public ICopier<Subscription> getCopier() {
    return Copier.INSTANCE;
  }

  @Mapper
  interface Copier extends ICopier<Subscription> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
