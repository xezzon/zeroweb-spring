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

package io.github.xezzon.zeroweb.app.entity;

import io.github.xezzon.zeroweb.app.App;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.domain.UpdateRequest;
import io.github.xezzon.zeroweb.core.trait.Merge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// `UpdateAppReq` 记录表示用于更新现有服务信息的请求体。
///
/// @param id 服务ID，标识要更新的特定服务。
/// @param name 服务名称，非空。
/// @param baseUrl 服务的基础访问路径，必须是一个有效的 URL。
/// @param ordinal 服务的显示顺序，值越小优先级越高。
/// @author xezzon
public record UpdateAppReq(
    @NotNull
    String id,
    @NotBlank @Size(max = DatabaseConstant.NORMAL_STRING_LENGTH)
    String name,
    @URL @NotNull @Size(max = DatabaseConstant.URL_LENGTH)
    String baseUrl,
    @NotNull
    Integer ordinal
) implements UpdateRequest<App> {

  @Override
  public App merge(final App oldValue) {
    return Converter.INSTANCE.merge(this, oldValue);
  }

  @Mapper
  interface Converter extends Merge<UpdateAppReq, App> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Override
    @Mapping(target = "id", source = "origin.id")
    @Mapping(target = "name", source = "value.name")
    @Mapping(target = "baseUrl", source = "value.baseUrl")
    @Mapping(target = "ordinal", source = "value.ordinal")
    App merge(UpdateAppReq value, App origin);
  }
}
