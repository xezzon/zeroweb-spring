package io.github.xezzon.zeroweb.dict;

import io.github.xezzon.zeroweb.common.trait.DbTrait;
import io.github.xezzon.zeroweb.dict.converter.DictImportReqConverter;
import io.github.xezzon.zeroweb.dict.domain.Dict;
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
