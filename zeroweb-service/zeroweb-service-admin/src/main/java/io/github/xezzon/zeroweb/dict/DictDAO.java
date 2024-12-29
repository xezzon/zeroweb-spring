package io.github.xezzon.zeroweb.dict;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.dict.domain.Dict;
import io.github.xezzon.zeroweb.dict.domain.Dict_;
import io.github.xezzon.zeroweb.dict.repository.DictRepository;
import io.github.xezzon.zeroweb.dict.repository.DictSpecs;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
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
    super(repository, Dict.class);
  }

  @Override
  public ICopier<Dict> getCopier() {
    return Copier.INSTANCE;
  }

  /**
   * 分页查询
   * @param odata 前端查询参数
   * @return 字典列表
   */
  @Override
  public Page<Dict> findAll(ODataQueryOption odata) {
    Specification<Dict> specification = DictSpecs.isDictTag();
    Sort sort = Sort.by(Order.asc(Dict_.CODE));
    return this.findAll(odata, specification, sort);
  }

  /**
   * 根据 tag、code 判断，如果字典存在，则跳过；否则保存
   * @param dict 字典信息
   */
  public void upsert(Dict dict) {
    Optional<Dict> exist = this.get().findByTagAndCode(dict.getTag(), dict.getCode());
    if (exist.isPresent()) {
      return;
    }
    this.get().save(dict);
  }

  /**
   * 更新字典项的状态
   * @param ids 需要更新的字典项ID集合
   * @param enabled 更新后的启用状态，true为启用，false为禁用
   * @return 更新影响的行数
   */
  @Transactional
  public long updateStatus(Collection<String> ids, Boolean enabled) {
    return super.update((root, criteriaUpdate, cb) -> criteriaUpdate
        .set(Dict_.enabled, enabled)
        .where(root.get(Dict_.id).in(ids))
    );
  }

  @Mapper
  interface Copier extends ICopier<Dict> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
