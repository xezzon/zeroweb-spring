package io.github.xezzon.zeroweb.common.redis;

import cn.hutool.core.util.RandomUtil;
import com.google.common.reflect.TypeToken;
import io.github.xezzon.zeroweb.common.jpa.TestEntity;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

/**
 * @author xezzon
 */
@SpringBootTest
@ActiveProfiles({"redis"})
class RedisTemplateFactoryTest {

  private static final String CACHE_KEY = "testEntity";
  @Resource
  private RedisTemplateFactory factory;
  private static final GenericContainer<?> REDIS_CONTAINER =
      new GenericContainer<>("redis:7-alpine");

  @BeforeAll
  static void beforeAll() {
    REDIS_CONTAINER
        .withExposedPorts(6379)
        .start();
  }

  @DynamicPropertySource
  @SuppressWarnings("unused")
  static void redisProperties(DynamicPropertyRegistry registry) {
    registry.add("REDIS_URL", () -> String.format(
        "%s:%s", REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getMappedPort(6379)
    ));
  }

  @Test
  void of() {
    List<TestEntity> excepts = new LinkedList<>();
    for (int i = 0, cnt = 8; i < cnt; i++) {
      TestEntity testEntity = new TestEntity();
      testEntity.setId(UUID.randomUUID().toString());
      testEntity.setField1(RandomUtil.randomString(8));
      testEntity.setField2(RandomUtil.randomString(6));
      excepts.add(testEntity);
    }
    factory.of(new TypeToken<>() {})
        .opsForValue()
        .set(CACHE_KEY, excepts);
    List<ChildEntity> actual = factory.<List<ChildEntity>>of(new TypeToken<>() {})
        .opsForValue()
        .get(CACHE_KEY);
    Assertions.assertIterableEquals(excepts, actual);
  }
}
