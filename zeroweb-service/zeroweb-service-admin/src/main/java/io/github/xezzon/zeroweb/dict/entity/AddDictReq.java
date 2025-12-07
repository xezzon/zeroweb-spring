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

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.dict.Dict;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 新增字典请求对象
///
/// 用于封装新增字典目或字典项的请求参数。
/// 该对象实现了 [Into] 接口，可以转换为 [Dict] 实体对象。
///
/// @author xezzon
@Getter
@Setter
public class AddDictReq implements Into<Dict> {

  /// 字典目
  ///
  /// 用于区分不同字典的命名空间，可为空。如果为空，系统将使用默认的字典目。
  @Nullable
  private String tag;
  /// 字典键
  ///
  /// 同一字典目下，键值唯一。约定：由用户定义的字典键，应该以小写字母开头；由系统生成的字典键，应该以大写字母开头。
  private String code;
  /// 字典值
  ///
  /// 字典项的显示名称。
  private String label;
  /// 排序号
  ///
  /// 数值越小，顺序越靠前。
  private Integer ordinal;
  /// 上级字典ID
  ///
  /// 用于将字典组织成树形结构，可为空。如果为空且 tag 不为空，则表示新增字典目。
  @Nullable
  private String parentId;

  /// 转换为字典实体对象
  ///
  /// @return 字典实体对象
  public Dict into() {
    return Converter.INSTANCE.from(this);
  }

  /// 请求对象到实体对象的转换器
  @Mapper
  interface Converter extends From<AddDictReq, Dict> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    /// 转换规则：
    /// - `editable` 设为常量 `true`
    /// - `enabled` 设为常量 `true`
    /// - 忽略 `id`，由数据库自动生成
    /// - 忽略 `children`，该字段为瞬态字段
    ///
    /// @param source 新增字典请求对象
    /// @return 字典实体对象
    @Mapping(target = "editable", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Override
    Dict from(AddDictReq source);
  }
}
