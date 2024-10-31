package io.github.xezzon.geom.openapi;

import io.github.xezzon.geom.common.jpa.BaseDAO;
import io.github.xezzon.geom.openapi.domain.Openapi;
import io.github.xezzon.geom.openapi.repository.OpenapiRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
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

  @Mapper
  interface Copier extends ICopier<Openapi> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
