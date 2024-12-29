package io.github.xezzon.zeroweb.third_party_app;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.domain.ThirdPartyApp_;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppRepository;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppSpec;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

  @Override
  public Page<ThirdPartyApp> findAll(ODataQueryOption odata) {
    Sort sort = ThirdPartyAppSpec.defaultSort();
    return this.findAll(odata, null, sort);
  }

  public Page<ThirdPartyApp> findAllWithUserId(ODataQueryOption odata, String userId) {
    Specification<ThirdPartyApp> specification = (root, query, cb) ->
        cb.equal(root.get(ThirdPartyApp_.OWNER_ID), userId);
    Sort sort = ThirdPartyAppSpec.defaultSort();
    return this.findAll(odata, specification, sort);
  }

  @Mapper
  interface Copier extends ICopier<ThirdPartyApp> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
