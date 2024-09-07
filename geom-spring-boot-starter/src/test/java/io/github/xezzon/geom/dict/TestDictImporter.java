package io.github.xezzon.geom.dict;

import jakarta.annotation.Priority;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Component
@Priority(-1)
public class TestDictImporter implements DictImporter {

  static List<DictImportReq> list = Collections.emptyList();

  @Override
  public void importDict(DictImportReqList reqList) {
    list = reqList.getDataList();
  }
}
