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

package io.github.xezzon.zeroweb.dict.converter;

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.dict.Dict;
import io.github.xezzon.zeroweb.dict.DictImportReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 字典导入请求转换器
///
/// 用于将 [DictImportReq] 转换为 [Dict] 实体对象。
/// 在转换过程中忽略部分字段，由业务逻辑填充默认值。
///
/// @author xezzon
@Mapper
public interface DictImportReqConverter extends From<DictImportReq, Dict> {

  /// 字典导入请求转换器实例
  DictImportReqConverter INSTANCE = Mappers.getMapper(DictImportReqConverter.class);

  /// 将字典导入请求转换为字典实体
  ///
  /// 转换规则：
  /// - 忽略 `parentId`，由上层调用者设置
  /// - 忽略 `id`，由数据库自动生成
  /// - `enabled` 设为常量 `true`
  /// - `editable` 设为常量 `false`
  /// - 忽略 `children`，该字段为瞬态字段
  /// - `tag` 使用请求值，如为空则使用 [Dict#DICT_TAG] 默认值
  ///
  /// @param source 字典导入请求
  /// @return 字典实体对象
  @Mapping(target = "parentId", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "enabled", constant = "true")
  @Mapping(target = "editable", constant = "false")
  @Mapping(target = "children", ignore = true)
  @Mapping(target = "tag", source = "tag", defaultValue = Dict.DICT_TAG)
  @Override
  Dict from(DictImportReq source);
}
