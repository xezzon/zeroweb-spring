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

package io.github.xezzon.zeroweb.dict.entity;

import static io.github.xezzon.zeroweb.common.constant.DatabaseConstant.NORMAL_STRING_LENGTH;

import io.github.xezzon.zeroweb.common.domain.UpdateRequest;
import io.github.xezzon.zeroweb.common.validator.Alphanumeric;
import io.github.xezzon.zeroweb.core.trait.Merge;
import io.github.xezzon.zeroweb.dict.Dict;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 修改字典请求对象
///
/// 用于封装修改字典项的请求参数。
///
/// @param id 字典 ID
/// @param code 字典键
/// @param label 字典值
/// @param ordinal 排序号
/// @param parentId 上级字典 ID
/// @param enabled 启用状态
public record ModifyDictReq(
    @NotNull
    String id,
    @Alphanumeric @NotBlank @Size(max = NORMAL_STRING_LENGTH)
    String code,
    @Size(max = NORMAL_STRING_LENGTH)
    String label,
    @NotNull
    Integer ordinal,
    String parentId,
    Boolean enabled
) implements UpdateRequest<Dict> {

  @Override
  public Dict merge(final Dict oldValue) {
    return Converter.INSTANCE.merge(this, oldValue);
  }

  /// 请求对象到实体对象的转换器
  @Mapper
  interface Converter extends Merge<ModifyDictReq, Dict> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Override
    @Mapping(target = "id", source = "origin.id")
    @Mapping(target = "tag", source = "origin.tag")
    @Mapping(target = "code", source = "value.code")
    @Mapping(target = "label", source = "value.label")
    @Mapping(target = "ordinal", source = "value.ordinal")
    @Mapping(target = "parentId", source = "value.parentId")
    @Mapping(target = "enabled", source = "value.enabled")
    @Mapping(target = "editable", source = "origin.editable")
    Dict merge(ModifyDictReq value, Dict origin);
  }
}
