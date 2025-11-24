package io.github.xezzon.zeroweb.dict;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xezzon
 */
@SpringBootTest
class DictScannerTest {

  @Test
  void dictScan() {
    List<DictImportReq> reqList = TestDictImporter.list;
    Assertions.assertTrue(reqList.parallelStream()
        .anyMatch(o -> Objects.equals(o.getCode(), TestEnum.class.getSimpleName()))
    );
    for (TestEnum value : TestEnum.values()) {
      Assertions.assertTrue(reqList.parallelStream()
          .anyMatch(o -> Objects.equals(value.getTag(), TestEnum.class.getSimpleName())
              && Objects.equals(value.getCode(), o.getCode())
          )
      );
    }
  }
}
