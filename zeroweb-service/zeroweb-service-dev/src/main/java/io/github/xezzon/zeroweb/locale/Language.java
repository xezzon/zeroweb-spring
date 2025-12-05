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

package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// 语言实体类。
///
/// 用于表示系统中的语言信息，底层基于字典实现。
/// 每个语言实体包含语言标签、描述、排序号以及启用状态等信息。
///
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_dict")
public class Language implements IEntity<String> {

  /// 定义语言字典的标签，值为 "Language"。
  public static final String LANGUAGE_DICT_TAG = "Language";
  /// 定义语言字典的父级 ID，值为 "1"。
  public static final String LANGUAGE_DICT_PARENT_ID = "1";

  /// 语言的唯一标识符。
  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /// 字典标签。
  ///
  /// 固定为 "Language"，不可为空，不可更新。
  @Column(name = "tag", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
  private String dictTag = LANGUAGE_DICT_TAG;
  /// 语言标签（例如："zh-CN", "en-US"）。
  ///
  /// 用于标识具体的语言，不可为空。
  ///
  /// @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Language">Accept-Language header</a>
  @Column(name = "code", nullable = false)
  String languageTag;
  /// 语言的描述信息。
  ///
  /// 可选，用于提供对语言更友好的说明。
  @Column(name = "label")
  String description;
  /// 语言的排序号。
  ///
  /// 数值越小，在列表中显示的顺序越靠前，不可为空。
  @Column(name = "ordinal", nullable = false)
  Integer ordinal;
  /// 字典上级 ID。
  ///
  /// 固定为 "1"。
  @Column(
      name = "parent_id", nullable = false, updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  @Setter(AccessLevel.NONE)
  private String parentId = LANGUAGE_DICT_PARENT_ID;
  /// 语言的启用状态。
  ///
  /// `true` 表示启用，`false` 表示禁用，不可为空。
  @Column(name = "enabled", nullable = false)
  Boolean enabled;
  /// 语言信息是否可编辑。
  ///
  /// 固定为 `true`，表示语言信息是可编辑的。
  @Column(name = "editable", nullable = false, updatable = false)
  @Setter(AccessLevel.NONE)
  private Boolean editable = true;
}
