package io.github.xezzon.geom.common.redis;

import static org.junit.jupiter.api.Assertions.*;

import io.github.xezzon.geom.common.jpa.TestEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author xezzon
 */
@SpringBootTest
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
