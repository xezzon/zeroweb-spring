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

package io.github.xezzon.zeroweb.locale.entity;

import static io.github.xezzon.zeroweb.common.constant.DatabaseConstant.NORMAL_STRING_LENGTH;

import io.github.xezzon.zeroweb.common.domain.UpdateRequest;
import io.github.xezzon.zeroweb.common.validator.Alphanumeric;
import io.github.xezzon.zeroweb.core.trait.Merge;
import io.github.xezzon.zeroweb.locale.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    @NotNull
    String id,
    @Alphanumeric @NotBlank @Size(max = NORMAL_STRING_LENGTH)
    String languageTag,
    @Size(max = NORMAL_STRING_LENGTH)
    String description,
    @NotNull
    Integer ordinal,
    Boolean enabled
) implements UpdateRequest<Language> {

  @Override
  public Language merge(final Language oldValue) {
    return Converter.INSTANCE.merge(this, oldValue);
  }

  @Mapper
  interface Converter extends Merge<ModifyLanguageReq, Language> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Override
    @Mapping(target = "id", source = "origin.id")
    @Mapping(target = "languageTag", source = "value.languageTag")
    @Mapping(target = "description", source = "value.description")
    @Mapping(target = "ordinal", source = "value.ordinal")
    @Mapping(target = "enabled", source = "value.enabled")
    Language merge(ModifyLanguageReq value, Language origin);
  }
}
