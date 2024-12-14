package io.github.xezzon.zeroweb.common.jpa;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.core.odata.ODataRequestParam;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author xezzon
 */
@SpringBootTest
@DirtiesContext
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

    TestEntity result = repository.findById(testEntity.getId()).orElseThrow();
    Assertions.assertEquals(testEntity1.getField1(), result.getField1());
    Assertions.assertEquals(testEntity.getField2(), result.getField2());
  }

  @Test
  void findAll() {
    final int loopTime = 16;
    for (int i = 0; i < loopTime; i++) {
      TestEntity testEntity = new TestEntity();
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
    Page<TestEntity> page1 = testEntityDAO.findAll(odata1, BaseSpecs.identicallyNotEqual(), null);
    Assertions.assertEquals(0, page1.getTotalElements());
    Assertions.assertEquals(0, page1.getContent().size());
    Assertions.assertEquals(pageSize, page1.getSize());
    Assertions.assertEquals(pageNum, page1.getNumber());
  }

  @Test
  void update() {
    TestEntity testEntity1 = new TestEntity();
    testEntity1.setField1(RandomUtil.randomString(8));
    testEntity1.setField2(RandomUtil.randomString(8));
    repository.save(testEntity1);
    TestEntity testEntity2 = new TestEntity();
    testEntity2.setField1(RandomUtil.randomString(8));
    testEntity2.setField2(RandomUtil.randomString(8));
    repository.save(testEntity2);

    String newValue = RandomUtil.randomString(7);
    int updated = testEntityDAO.updateField2ByField1(testEntity1.getField1(), newValue);
    Assertions.assertEquals(1, updated);
    TestEntity result = repository.findById(testEntity1.getId()).orElseThrow();
    Assertions.assertEquals(newValue, result.getField2());
    TestEntity another = repository.findById(testEntity2.getId()).orElseThrow();
    Assertions.assertEquals(testEntity2.getField2(), another.getField2());
  }
}
