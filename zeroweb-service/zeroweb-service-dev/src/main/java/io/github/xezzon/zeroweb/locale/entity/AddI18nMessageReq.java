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

import io.github.xezzon.zeroweb.common.validator.Alphanumeric;
import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.locale.I18nMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 新增国际化内容的请求参数。
///
/// @param namespace 国际化内容命名空间。
/// @param messageKey 国际化内容的键。
/// @author xezzon
public record AddI18nMessageReq(
    @Alphanumeric @NotBlank @Size(max = 255)
    String namespace,
    @Alphanumeric @NotBlank @Size(max = 255)
    String messageKey
) implements Into<I18nMessage> {

  /// 将当前请求对象转换为 [I18nMessage] 实体。
  ///
  /// @return 转换后的 [I18nMessage] 实体。
  @Override
  public I18nMessage into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddI18nMessageReq, I18nMessage> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "id", ignore = true)
    @Override
    I18nMessage from(AddI18nMessageReq source);
  }
}
