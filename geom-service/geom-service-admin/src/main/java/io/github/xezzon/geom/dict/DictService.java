package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.common.exception.RepeatDataException;
import io.github.xezzon.geom.dict.domain.Dict;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class DictService {

  private final DictDAO dictDAO;

  public DictService(DictDAO dictDAO) {
    this.dictDAO = dictDAO;
  }

  /**
   * 新增字典
   * @param dict 字典项
   * @throws RepeatDataException 字典键冲突
   */
  protected void addDict(Dict dict) {
    /* 前置校验 */
    checkRepeat(dict);
    /* 持久化 */
    dictDAO.get().save(dict);
  }

  /**
   * 修改字典
   * @param dict 字典项
   * @throws RepeatDataException 字典键冲突
   */
  protected void modifyDict(Dict dict) {
    Dict entity = dictDAO.get().getReferenceById(dict.getId());
    dictDAO.getCopier().copy(dict, entity);
    /* 前置校验 */
    this.checkRepeat(entity);
    /* 持久化 */
    dictDAO.get().save(entity);
  }

  protected void updateDictStatus(Collection<String> ids, Boolean enabled) {
    if (ids.isEmpty()) {
      return;
    }
    dictDAO.updateStatus(ids, enabled);
  }

  private void checkRepeat(Dict dict) {
    Optional<Dict> exist = dictDAO.get().findByTagAndCode(dict.getTag(), dict.getCode());
    if (exist.isPresent() && !Objects.equals(dict.getId(), exist.get().getId())) {
      // 存在冲突的字典项
      throw new RepeatDataException(MessageFormat.format("字典`{0}`已存在", dict.getCode()));
    }
  }
}
