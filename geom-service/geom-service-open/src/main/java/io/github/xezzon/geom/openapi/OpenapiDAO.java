package io.github.xezzon.geom.openapi;

import io.github.xezzon.geom.common.jpa.BaseDAO;
import io.github.xezzon.geom.core.odata.ODataQueryOption;
import io.github.xezzon.geom.openapi.domain.Openapi;
import io.github.xezzon.geom.openapi.domain.Openapi_;
import io.github.xezzon.geom.openapi.repository.OpenapiRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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

  @Mapper
  interface Copier extends ICopier<Openapi> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
