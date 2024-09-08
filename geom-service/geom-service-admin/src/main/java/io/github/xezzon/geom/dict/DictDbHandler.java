package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.common.constant.DatabaseConstant;
import io.github.xezzon.geom.common.trait.DbTrait;
import io.github.xezzon.geom.dict.converter.DictImportReqConverter;
import io.github.xezzon.geom.dict.domain.Dict;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class DictDbHandler implements DictImporter, DbTrait {

  private final DictDAO dictDAO;

  public DictDbHandler(DictDAO dictDAO) {
    this.dictDAO = dictDAO;
  }

  @Override
  public void importDict(DictImportReqList reqList) {
    List<Dict> dictList = reqList.getDataList().parallelStream()
        .map(DictImportReqConverter.INSTANCE::from)
        .toList();
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
    System.out.println();
  }
}
