package io.github.xezzon.geom.dict;

import io.github.xezzon.geom.common.trait.DbTrait;
import io.github.xezzon.geom.dict.converter.DictImportReqConverter;
import io.github.xezzon.geom.dict.domain.Dict;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class DictDbHandler implements DictImporter, DbTrait {

  private final DictService dictService;

  public DictDbHandler(DictService dictService) {
    this.dictService = dictService;
  }

  @Override
  public void importDict(DictImportReqList reqList) {
    List<Dict> dictList = reqList.getDataList().parallelStream()
        .map(DictImportReqConverter.INSTANCE::from)
        .toList();
    dictService.importDict(dictList);
  }
}
