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

/**
 * @author xezzon
 */
@SuppressWarnings("ClassCanBeRecord")
public class GsonRedisSerializer<T> implements RedisSerializer<@NonNull T> {

  private static final Gson GSON = new Gson();
  private final Type type;

  public GsonRedisSerializer(final TypeToken<@NonNull T> typeToken) {
    this.type = typeToken.getType();
  }

  @Override
  public byte @NonNull [] serialize(final @Nullable T value) throws SerializationException {
    return GSON.toJson(value).getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public @Nullable T deserialize(byte @Nullable [] bytes) throws SerializationException {
    if (bytes == null) {
      return null;
    }
    return GSON.fromJson(new String(bytes, StandardCharsets.UTF_8), type);
  }
}
