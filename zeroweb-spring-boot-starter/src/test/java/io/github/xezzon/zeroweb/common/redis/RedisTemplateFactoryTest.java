package io.github.xezzon.zeroweb.common.redis;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.xezzon.zeroweb.common.jpa.TestEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author xezzon
 */
@SpringBootTest
@ActiveProfiles({"redis"})
@DirtiesContext
class RedisTemplateFactoryTest {

  @Resource
  private RedisTemplateFactory factory;

  @Test
  void of() {
    RedisTemplate<String, TestEntity> redisTemplate = factory.of(TestEntity.class);
    assertNotNull(redisTemplate);
  }

  @Test
  void genericRedisTemplate() {
    RedisTemplate<String, TestEntity> redisTemplate = factory.genericRedisTemplate();
    assertNotNull(redisTemplate);
  }
}
