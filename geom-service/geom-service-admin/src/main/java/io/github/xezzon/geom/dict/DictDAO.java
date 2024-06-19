package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.common.jpa.BaseDAO;
import io.github.xezzon.geom.dict.domain.Dict;
import io.github.xezzon.geom.dict.domain.QDict;
import io.github.xezzon.geom.dict.repository.DictRepository;
import jakarta.transaction.Transactional;
import java.util.Collection;
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

  @Transactional
  public long updateStatus(Collection<String> ids, Boolean enabled) {
    QDict qDict = this.getQuery();
    return this.getQueryFactory()
        .update(qDict)
        .set(qDict.enabled, enabled)
        .where(qDict.id.in(ids))
        .execute()
    ;
  }

  @Mapper
  interface Copier extends ICopier<Dict> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
