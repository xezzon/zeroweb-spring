package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.common.jpa.BaseDAO;
import io.github.xezzon.geom.common.odata.ODataQueryOption;
import io.github.xezzon.geom.dict.domain.Dict;
import io.github.xezzon.geom.dict.domain.DictSpecs;
import io.github.xezzon.geom.dict.domain.Dict_;
import io.github.xezzon.geom.dict.domain.QDict;
import io.github.xezzon.geom.dict.repository.DictRepository;
import jakarta.transaction.Transactional;
import java.util.Collection;
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

  /**
   * 分页查询
   * @param odata 前端查询参数
   * @param criteria 后端查询参数
   * @return 字典列表
   */
  public Page<Dict> findAll(ODataQueryOption odata) {
    Specification<Dict> specification = DictSpecs.isDictTag();
    Sort sort = Sort.by(Order.asc(Dict_.CODE));
    return this.findAll(odata, specification, sort);
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
