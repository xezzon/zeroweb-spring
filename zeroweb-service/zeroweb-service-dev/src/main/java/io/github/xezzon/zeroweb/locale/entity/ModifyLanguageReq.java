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
import org.mapstruct.factory.Mappers;

/// 修改语言的请求参数
///
/// @param id 需要被修改的语言的ID
/// @param languageTag 语言标签
/// @param description 语言描述
/// @param ordinal 排序序号
/// @param enabled 是否启用
/// @author xezzon
public record ModifyLanguageReq(
    String id,
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
  interface Converter extends From<ModifyLanguageReq, Language> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Override
    Language from(ModifyLanguageReq source);
  }
}
