package io.github.xezzon.zeroweb.openapi;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.openapi.domain.Openapi;
import io.github.xezzon.zeroweb.openapi.domain.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.domain.Openapi_;
import io.github.xezzon.zeroweb.openapi.repository.OpenapiRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class OpenapiDAO extends BaseDAO<Openapi, String, OpenapiRepository> {

  protected OpenapiDAO(OpenapiRepository repository) {
    super(repository, Openapi.class);
  }

  @Override
  public ICopier<Openapi> getCopier() {
    return Copier.INSTANCE;
  }

  @Override
  public Page<Openapi> findAll(ODataQueryOption odata) {
    Sort sort = Sort.by(Order.asc(Openapi_.CODE));
    return super.findAll(odata, null, sort);
  }

  public Page<Openapi> listPublishedOpenapi(ODataQueryOption odata) {
    Specification<Openapi> spec = (root, query, cb) ->
        cb.equal(root.get(Openapi_.status), OpenapiStatus.PUBLISHED);
    Sort sort = Sort.by(Order.asc(Openapi_.CODE));
    return super.findAll(odata, spec, sort);
  }

  @Mapper
  interface Copier extends ICopier<Openapi> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
