/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.xezzon.zeroweb.common.redis;

import com.google.common.reflect.TypeToken;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/// [RedisTemplate] 的工厂类，用于创建具有特定值类型的 [RedisTemplate] 实例。
///
/// 该类通过注入 [RedisConnectionFactory] 和使用 [StringRedisSerializer] 作为键序列化器，
/// 并结合 [GsonRedisSerializer] 作为值序列化器来构建 [RedisTemplate]。
///
/// @author xezzon
@Configuration
@Profile("redis")
public class RedisTemplateFactory {

  /// Redis 连接工厂，用于创建 Redis 连接。
  private final RedisConnectionFactory connectionFactory;
  /// Redis 键的序列化器，默认为 [StringRedisSerializer]。
  private final RedisSerializer<@NonNull String> keySerializer;

  /// 构造函数，通过注入 [RedisConnectionFactory] 初始化工厂。
  ///
  /// @param connectionFactory Redis 连接工厂
  public RedisTemplateFactory(final RedisConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
    this.keySerializer = new StringRedisSerializer();
  }

  /// 创建一个指定值类型的 [RedisTemplate] 实例。
  ///
  /// 使用方法如下：
  /// ```java
  /// public class AnyService {
  ///   private final RedisTemplate<String, Any> anyRedisTemplate;
  ///   public AnyService(RedisTemplateFactory factory) {
  ///     this.anyRedisTemplate = factory.of(new TypeToken<>() {});
  ///   }
  /// }
  /// ```
  ///
  /// @param typeToken 用于指定 [RedisTemplate] 值类型的 [TypeToken]
  /// @param <T> 值类型
  /// @return 具有指定值类型的 [RedisTemplate] 实例
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
