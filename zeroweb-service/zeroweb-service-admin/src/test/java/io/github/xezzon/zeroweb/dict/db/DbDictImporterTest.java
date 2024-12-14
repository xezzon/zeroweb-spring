package io.github.xezzon.zeroweb.dict.db;

import io.github.xezzon.zeroweb.dict.DictDbHandler;
import io.github.xezzon.zeroweb.dict.DictImporter;
import io.github.xezzon.zeroweb.dict.TestEnum;
import io.github.xezzon.zeroweb.dict.domain.Dict;
import io.github.xezzon.zeroweb.dict.repository.DictRepository;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author xezzon
 */
@SpringBootTest
@DirtiesContext
class DbDictImporterTest {

  @Resource
  private DictImporter dictImporter;
  @Resource
  private DictRepository repository;

  @Test
  void importDict() {
    Assertions.assertInstanceOf(DictDbHandler.class, dictImporter);
    List<Dict> reqList = repository.findAll();
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
