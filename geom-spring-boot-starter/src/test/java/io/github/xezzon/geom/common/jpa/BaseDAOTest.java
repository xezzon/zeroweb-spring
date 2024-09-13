package io.github.xezzon.geom.common.jpa;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.common.odata.ODataQueryOption;
import io.github.xezzon.geom.common.odata.ODataRequestParam;
import jakarta.annotation.Resource;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

/**
 * @author xezzon
 */
@SpringBootTest
class BaseDAOTest {

  @Resource
  private TestEntityDAO testEntityDAO;
  @Resource
  private TestEntityRepository repository;


  @Test
  void partialUpdate() {
    TestEntity testEntity = new TestEntity();
    testEntity.setField1(RandomUtil.randomString(8));
    testEntity.setField2(RandomUtil.randomString(8));
    repository.save(testEntity);
    TestEntity testEntity1 = new TestEntity();
    testEntity1.setId(testEntity.getId());
    testEntity1.setField1(RandomUtil.randomString(8));
    testEntityDAO.partialUpdate(testEntity1);

    Optional<TestEntity> result = repository.findById(testEntity.getId());
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(testEntity1.getField1(), result.get().getField1());
    Assertions.assertEquals(testEntity.getField2(), result.get().getField2());
  }

  @Test
  void findAll() {
    final int loopTime = 16;
    for (int i = 0; i < loopTime; i++) {
      TestEntity testEntity = new TestEntity();
      if (RandomUtil.randomBoolean()) {
        testEntity.setId(UUID.randomUUID().toString());
      }
      testEntity.setField1(RandomUtil.randomString(8));
      testEntity.setField2(RandomUtil.randomString(8));
      repository.save(testEntity);
    }
    final int pageNum = 2;
    final int pageSize = 2;
    ODataRequestParam param = new ODataRequestParam(pageSize, pageSize * pageNum);
    ODataQueryOption odata = param.into();
    Page<TestEntity> page = testEntityDAO.findAll(odata);
    Assertions.assertEquals(loopTime, page.getTotalElements());
    Assertions.assertEquals(pageSize, page.getContent().size());
    Assertions.assertEquals(pageSize, page.getSize());
    Assertions.assertEquals(pageNum, page.getNumber());

    ODataQueryOption odata1 = ODataQueryOption.builder()
        .top(pageSize)
        .skip(pageNum * pageSize)
        .build();
    Page<TestEntity> page1 = testEntityDAO.findAll(odata1, BaseSpecs.FALSE(), null);
    Assertions.assertEquals(0, page1.getTotalElements());
    Assertions.assertEquals(0, page1.getContent().size());
    Assertions.assertEquals(pageSize, page1.getSize());
    Assertions.assertEquals(pageNum, page1.getNumber());
  }
}
