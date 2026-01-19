/*
 * SPDX-FileCopyrightText: Copyright (C) 2025-2026 xezzon
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

package io.github.xezzon.zeroweb.setting.entity;

import io.github.xezzon.zeroweb.common.domain.UpdateRequest;
import io.github.xezzon.zeroweb.core.trait.Merge;
import io.github.xezzon.zeroweb.setting.Setting;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 更新业务参数约束请求
///
/// 用于更新现有业务参数的约束定义和参数值。
/// 通过ID标识要更新的参数，同时更新schema和value字段。
/// @param id 需要更新的业务参数ID
/// @param schema 新的参数约束定义，JSON Schema 格式
/// @param value 新的参数值，JSON格式
/// @author xezzon
public record UpdateSchemaRequest(
    @NotNull
    String id,
    @NotNull
    String schema,
    Map<String, Object> value
) implements UpdateRequest<Setting> {

  @Override
  public Setting merge(final Setting oldValue) {
    return Converter.INSTANCE.merge(this, oldValue);
  }

  @Mapper
  interface Converter extends Merge<UpdateSchemaRequest, Setting> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Override
    @Mapping(target = "id", source = "origin.id")
    @Mapping(target = "schema", source = "value.schema")
    @Mapping(target = "value", source = "value.value")
    Setting merge(UpdateSchemaRequest value, Setting origin);
  }
}
