package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.common.jpa.BaseDAO;
import io.github.xezzon.geom.dict.domain.Dict;
import io.github.xezzon.geom.dict.domain.QDict;
import io.github.xezzon.geom.dict.repository.DictRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class DictDAO extends BaseDAO<Dict, String, DictRepository> {

  DictDAO(DictRepository repository) {
    super(repository);
  }

  @Override
  public ICopier<Dict> getCopier() {
    return Copier.INSTANCE;
  }

  @Override
  protected QDict getQuery() {
    return QDict.dict;
  }

  @Mapper
  interface Copier extends ICopier<Dict> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
