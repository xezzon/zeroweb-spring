package io.github.xezzon.geom.third_party_app;

import io.github.xezzon.geom.common.jpa.BaseDAO;
import io.github.xezzon.geom.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.geom.third_party_app.repository.ThirdPartyAppRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class ThirdPartyAppDAO extends BaseDAO<ThirdPartyApp, String, ThirdPartyAppRepository> {

  protected ThirdPartyAppDAO(ThirdPartyAppRepository repository) {
    super(repository, ThirdPartyApp.class);
  }

  @Override
  public ICopier<ThirdPartyApp> getCopier() {
    return Copier.INSTANCE;
  }

  @Mapper
  interface Copier extends ICopier<ThirdPartyApp> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
