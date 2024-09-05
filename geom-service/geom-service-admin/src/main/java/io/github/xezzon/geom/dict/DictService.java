package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.common.constant.DatabaseConstant;
import io.github.xezzon.geom.common.exception.RepeatDataException;
import io.github.xezzon.geom.common.odata.ODataQueryOption;
import io.github.xezzon.geom.dict.domain.Dict;
import jakarta.transaction.Transactional;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
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
   * 根据OData查询选项获取字典分页列表
   * @param odata OData查询选项
   * @return 字典分页列表
   */
  protected Page<Dict> pagedList(ODataQueryOption odata) {
      return dictDAO.findAll(odata);
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

  /**
   * 更新字典状态
   * @param ids 字典ID集合
   * @param enabled 更新后的字典启用状态
   */
  protected void updateDictStatus(Collection<String> ids, Boolean enabled) {
    if (ids.isEmpty()) {
      return;
    }
    dictDAO.updateStatus(ids, enabled);
  }

  /**
   * 删除字典，并递归删除其子级
   * @param ids 字典ID集合
   */
  @Transactional
  protected void remove(Collection<String> ids) {
    while (!ids.isEmpty()) {
      dictDAO.get().deleteAllByIdInBatch(ids);
      List<Dict> children = dictDAO.get().findByParentIdIn(ids);
      ids = children.parallelStream()
          .map(Dict::getId)
          .collect(Collectors.toSet());
    }
  }

  /**
   * 获取字典目下所有的字典项，按排序号升序排列
   * @param tag 字典目编码
   * @return 字典项列表
   */
  protected List<Dict> getDictItemList(String tag) {
    return dictDAO.get().findByTagOrderByOrdinalAsc(tag);
  }

  /**
   * 批量导入字典
   * @param dictList 字典列表
   */
  protected void importDict(List<Dict> dictList) {
    List<Dict> tagList = dictList.stream()
        .filter(o -> Objects.equals(o.getTag(), Dict.DICT_TAG))
        .toList();
    for (Dict tag : tagList) {
      tag.setParentId(DatabaseConstant.ROOT_ID);
      dictDAO.upsert(tag);
    }
    List<Dict> itemList = dictList.parallelStream()
        .filter(o -> !Objects.equals(o.getTag(), Dict.DICT_TAG))
        .toList();
    for (Dict item : itemList) {
      Optional<Dict> parentDict = dictDAO.get().findByTagAndCode(Dict.DICT_TAG, item.getTag());
      if (parentDict.isEmpty()) {
        continue;
      }
      item.setParentId(parentDict.get().getId());
      dictDAO.upsert(item);
    }
  }

  /**
   * 重复性校验
   * 两个不同的字典之间，不能具有相同的字典目与字典码
   * @param dict 待检查的字典 至少包含 字典目编码、字典码、ID（可为空）字段
   */
  private void checkRepeat(Dict dict) {
    Optional<Dict> exist = dictDAO.get().findByTagAndCode(dict.getTag(), dict.getCode());
    if (exist.isPresent() && !Objects.equals(dict.getId(), exist.get().getId())) {
      // 存在冲突的字典项
      throw new RepeatDataException(MessageFormat.format("字典`{0}`已存在", dict.getCode()));
    }
  }
}
