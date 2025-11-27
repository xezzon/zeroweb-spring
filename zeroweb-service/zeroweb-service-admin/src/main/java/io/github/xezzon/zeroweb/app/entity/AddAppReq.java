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
import org.hibernate.validator.constraints.URL;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 新增服务
///
/// @param name 服务名称
/// @param baseUrl 基础访问路径
/// @param ordinal 排序值 顺序越小越靠前
/// @author xezzon
public record AddAppReq(
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
  interface Converter extends From<AddAppReq, App> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "id", ignore = true)
    @Override
    App from(AddAppReq source);
  }
}
