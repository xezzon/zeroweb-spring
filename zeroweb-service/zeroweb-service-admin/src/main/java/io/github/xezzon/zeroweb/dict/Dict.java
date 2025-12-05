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

package io.github.xezzon.zeroweb.dict;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import io.github.xezzon.zeroweb.core.trait.IDict;
import io.github.xezzon.zeroweb.core.tree.ITreeNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// 字典
///
/// 字典是一组提供给用户选择的值。
///
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_dict")
public class Dict implements IEntity<String>, IDict, ITreeNode<Dict, String> {

  /// 字典目的 `tag` 字段的值
  public static final String DICT_TAG = "DICT";

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /// 字典目
  ///
  /// 字典目用于区分不同字典的命名空间。
  ///
  /// 字典目本身可以视为特殊的字典项，该值为`DICT`。
  @Column(name = "tag", nullable = false, updatable = false)
  String tag;
  /// 字典键
  ///
  /// 同一字典目下，键值唯一。
  ///
  /// 约定：由用户定义的字典键，应该以小写字母开头；由系统生成的字典键，应该以大写字母开头。
  @Column(name = "code", nullable = false)
  String code;
  /// 字典值
  @Column(name = "label")
  String label;
  /// 排序号
  ///
  /// 数值越小，顺序越靠前。
  @Column(name = "ordinal", nullable = false)
  Integer ordinal;
  /// 上级字典ID
  ///
  /// 用于将字典组织成树形结构。
  ///
  /// 字典目可视为特殊的字典项，该值为`0`。
  @Column(name = "parent_id", nullable = false, length = DatabaseConstant.ID_LENGTH)
  String parentId;
  /// 启用状态
  ///
  /// 约定：前端的新增表单应该只显示启用状态为`是`的数据。
  @Column(name = "enabled", nullable = false)
  Boolean enabled;
  /// 是否可编辑
  ///
  /// 是否可编辑为`否`的数据，不允许修改字典键，不允许删除，否则会造成反序列化问题。
  @Column(name = "editable", nullable = false)
  Boolean editable;
  /// 子级字典
  @Transient
  List<Dict> children;

  public int getOrdinal() {
    return this.ordinal;
  }
}
