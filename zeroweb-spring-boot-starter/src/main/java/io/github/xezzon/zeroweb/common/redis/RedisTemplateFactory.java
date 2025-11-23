package io.github.xezzon.zeroweb.common.redis;

import com.google.common.reflect.TypeToken;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author xezzon
 */
@Configuration
@Profile("redis")
public class RedisTemplateFactory {

  private final RedisConnectionFactory connectionFactory;
  private final RedisSerializer<@NonNull String> keySerializer;

  public RedisTemplateFactory(final RedisConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
    this.keySerializer = new StringRedisSerializer();
  }

  /**
   * 指定值类型的 Redis 处理器 使用方法如下：
   * <pre>
   * public class AnyService {
   *   private final RedisTemplate&lt;String, Any&gt; anyRedisTemplate;
   *   public AnyService(RedisTemplateFactory factory) {
   *     this.anyRedisTemplate = factory.of(new TypeToken&lt;&gt;() {});
   *   }
   * }
   * </pre>
   */
  public <T> RedisTemplate<String, T> of(final @NonNull TypeToken<@NonNull T> typeToken) {
    RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setKeySerializer(keySerializer);
    redisTemplate.setHashKeySerializer(keySerializer);
    final RedisSerializer<@NonNull T> valueSerializer = new GsonRedisSerializer<>(typeToken);
    redisTemplate.setValueSerializer(valueSerializer);
    redisTemplate.setHashValueSerializer(valueSerializer);
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }
}
