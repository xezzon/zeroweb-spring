package io.github.xezzon.zeroweb.third_party_app.internal;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppRepository;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppSpec;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

/// @author xezzon
@Repository
@NullMarked
public class ThirdPartyAppDAO extends BaseDAO<ThirdPartyApp, String, ThirdPartyAppRepository> {

  protected ThirdPartyAppDAO(ThirdPartyAppRepository repository) {
    super(repository, ThirdPartyApp.class);
  }

  @Override
  public ICopier<ThirdPartyApp> getCopier() {
    return Copier.INSTANCE;
  }

  @Override
  public Page<ThirdPartyApp> findAll(final ODataQueryOption odata) {
    Sort sort = ThirdPartyAppSpec.defaultSort();
    return this.findAll(odata, null, sort);
  }

  @Mapper
  interface Copier extends ICopier<ThirdPartyApp> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
