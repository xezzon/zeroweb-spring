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
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/// 使用 Gson 进行序列化和反序列化的 Redis 序列化器。
/// 此序列化器能够将 Java 对象序列化为 UTF-8 编码的 JSON 字符串字节数组，
/// 并将字节数组反序列化回 Java 对象。
///
/// @param <T> 序列化和反序列化的对象类型
/// @author xezzon
@SuppressWarnings("ClassCanBeRecord")
public class GsonRedisSerializer<T> implements RedisSerializer<@NonNull T> {

  /// 用于 JSON 序列化和反序列化的 Gson 实例。
  private static final Gson GSON = new Gson();
  /// 当前序列化器处理的对象的具体类型，用于 Gson 的泛型反序列化。
  private final Type type;

  /// 构造一个新的 `GsonRedisSerializer` 实例。
  ///
  /// @param typeToken 用于获取泛型类型的 [TypeToken]，确保在运行时可以正确反序列化泛型对象。
  public GsonRedisSerializer(final TypeToken<@NonNull T> typeToken) {
    this.type = typeToken.getType();
  }

  /// 将给定对象序列化为字节数组。
  ///
  /// @param value 要序列化的对象，可以为 `null`。
  /// @return 对象的 UTF-8 编码 JSON 字节数组。如果输入值为 `null`，则返回表示 "null" 的字节数组。
  /// @throws SerializationException 如果序列化过程中发生错误。
  @Override
  public byte @NonNull [] serialize(final @Nullable T value) {
    return GSON.toJson(value).getBytes(StandardCharsets.UTF_8);
  }

  /// 从字节数组反序列化为对象。
  ///
  /// @param bytes 包含对象 UTF-8 编码 JSON 数据的字节数组，可以为 `null`。
  /// @return 反序列化后的对象实例。如果输入字节数组为 `null`，则返回 `null`。
  /// @throws SerializationException 如果反序列化过程中发生错误。
  @Override
  public @Nullable T deserialize(byte @Nullable [] bytes) {
    if (bytes == null) {
      return null;
    }
    return GSON.fromJson(new String(bytes, StandardCharsets.UTF_8), type);
  }
}
