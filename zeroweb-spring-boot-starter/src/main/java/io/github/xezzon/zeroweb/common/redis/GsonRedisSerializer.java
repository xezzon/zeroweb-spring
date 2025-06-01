package io.github.xezzon.zeroweb.common.redis;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @author xezzon
 */
public class GsonRedisSerializer<T> implements RedisSerializer<T> {

  private static final Gson GSON = new Gson();
  private final Type type;

  public GsonRedisSerializer(final TypeToken<T> typeToken) {
    this.type = typeToken.getType();
  }

  @Override
  public byte[] serialize(final T value) throws SerializationException {
    return GSON.toJson(value).getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public T deserialize(final byte[] bytes) throws SerializationException {
    return GSON.fromJson(new String(bytes, StandardCharsets.UTF_8), type);
  }
}
