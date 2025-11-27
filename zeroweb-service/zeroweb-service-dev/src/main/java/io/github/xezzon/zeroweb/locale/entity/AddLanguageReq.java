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

package io.github.xezzon.zeroweb.locale.entity;

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.locale.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 新增语言的请求参数
///
/// @param languageTag 语言标签
/// @param description 语言描述
/// @param ordinal 排序
/// @param enabled 是否启用
/// @author xezzon
public record AddLanguageReq(
    String languageTag,
    String description,
    Integer ordinal,
    Boolean enabled
) implements Into<Language> {

  @Override
  public Language into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddLanguageReq, Language> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", defaultValue = "true")
    @Override
    Language from(AddLanguageReq source);
  }
}
