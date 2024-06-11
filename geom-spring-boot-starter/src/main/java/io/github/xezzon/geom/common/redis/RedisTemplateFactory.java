package io.github.xezzon.geom.common.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author xezzon
 */
@Configuration
@ConditionalOnExpression("!'${REDIS_URL:}'.empty")
public class RedisTemplateFactory {

  private final RedisConnectionFactory connectionFactory;
  private final RedisSerializer<String> keySerializer;
  private final ObjectMapper objectMapper;

  public RedisTemplateFactory(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
    this.connectionFactory = connectionFactory;
    this.keySerializer = new StringRedisSerializer();
    this.objectMapper = objectMapper;
  }

  /**
   * 指定值类型的 Redis 处理器 使用方法如下：
   * <pre>
   * public class AnyService {
   *   private final RedisTemplate&lt;String, Any&gt; anyRedisTemplate;
   *   public AnyService(RedisTemplateFactory factory) {
   *     this.anyRedisTemplate = factory.of(Any.class)
   *   }
   * }
   * </pre>
   */
  public <T> RedisTemplate<String, T> of(Class<T> tClass) {
    RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setKeySerializer(keySerializer);
    redisTemplate.setHashKeySerializer(keySerializer);
    RedisSerializer<T> valueSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, tClass);
    redisTemplate.setValueSerializer(valueSerializer);
    redisTemplate.setHashValueSerializer(valueSerializer);
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }

  /**
   * 常规 Redis 处理器
   */
  @Bean
  public <T> RedisTemplate<String, T> genericRedisTemplate() {
    RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setKeySerializer(keySerializer);
    redisTemplate.setKeySerializer(keySerializer);
    RedisSerializer<?> valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
    redisTemplate.setValueSerializer(valueSerializer);
    redisTemplate.setHashValueSerializer(valueSerializer);
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }
}
