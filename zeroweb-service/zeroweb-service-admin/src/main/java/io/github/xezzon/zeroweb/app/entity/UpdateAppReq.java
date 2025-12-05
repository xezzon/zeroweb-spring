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

package io.github.xezzon.zeroweb.app.entity;

import io.github.xezzon.zeroweb.app.App;
import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/// `UpdateAppReq` 记录表示用于更新现有服务信息的请求体。
///
/// @param id 服务ID，标识要更新的特定服务。
/// @param name 服务名称，非空。
/// @param baseUrl 服务的基础访问路径，必须是一个有效的 URL。
/// @param ordinal 服务的显示顺序，值越小优先级越高。
/// @author xezzon
public record UpdateAppReq(
    String id,
    @NotNull
    String name,
    @URL
    String baseUrl,
    Integer ordinal
) implements Into<App> {

  @Override
  public App into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<UpdateAppReq, App> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Override
    App from(UpdateAppReq source);
  }
}
