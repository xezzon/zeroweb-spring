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
import io.github.xezzon.zeroweb.locale.Translation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 更新或新增国际化文本的请求体
///
/// @param namespace 命名空间 与国际化内容的命名空间一致
/// @param messageKey 国际化内容
/// @param language 国际化语言标签
/// @param content 国际化文本
/// @author xezzon
public record UpsertTranslationReq(
    String namespace,
    String messageKey,
    String language,
    String content
) implements Into<Translation> {

  @Override
  public Translation into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<UpsertTranslationReq, Translation> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "id", ignore = true)
    @Override
    Translation from(UpsertTranslationReq source);
  }
}
