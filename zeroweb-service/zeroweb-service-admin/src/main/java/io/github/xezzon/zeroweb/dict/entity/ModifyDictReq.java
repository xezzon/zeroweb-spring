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

package io.github.xezzon.zeroweb.dict.entity;

import io.github.xezzon.zeroweb.common.validator.Alphanumeric;
import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
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
/// 该对象实现了 [Into] 接口，可以转换为 [Dict] 实体对象。
///
/// @param id 字典ID
/// @param code 字典键
/// @param label 字典值
/// @param ordinal 排序号
/// @param parentId 上级字典ID
/// @param enabled 启用状态
public record ModifyDictReq(
    @NotNull
    String id,
    @Alphanumeric @NotBlank @Size(max = 255)
    String code,
    @Size(max = 255)
    String label,
    @NotNull
    Integer ordinal,
    String parentId,
    Boolean enabled
) implements Into<Dict> {

  /// 转换为字典实体对象
  ///
  /// @return 字典实体对象
  @Override
  public Dict into() {
    return Converter.INSTANCE.from(this);
  }

  /// 请求对象到实体对象的转换器
  @Mapper
  interface Converter extends From<ModifyDictReq, Dict> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    /// 转换规则：
    /// - 忽略 `editable`，不允许修改
    /// - 忽略 `tag`，不允许修改
    /// - 忽略 `children`，该字段为瞬态字段
    ///
    /// @param source 修改字典请求对象
    /// @return 字典实体对象
    @Mapping(target = "editable", ignore = true)
    @Mapping(target = "tag", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Override
    Dict from(ModifyDictReq source);
  }
}
