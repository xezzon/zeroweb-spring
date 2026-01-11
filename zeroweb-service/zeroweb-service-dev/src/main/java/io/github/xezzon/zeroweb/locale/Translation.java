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

package io.github.xezzon.zeroweb.locale;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// 国际化翻译文本实体类。
///
/// 用于存储特定语言下国际化消息的翻译内容。
/// 每个翻译条目由唯一的 ID、命名空间、消息键、语言标签和实际翻译文本组成。
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_translation")
public class Translation implements IEntity<String> {

  /// 翻译条目的唯一标识符。
  ///
  /// 作为主键，不可为空，不可更新，长度由 [DatabaseConstant#ID_LENGTH] 定义。
  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /// 国际化内容的命名空间。
  ///
  /// 与 [I18nMessage] 中的命名空间一致，用于关联到特定的国际化消息，不可为空。
  @Column(name = "namespace", nullable = false)
  String namespace;
  /// 国际化消息的键。
  ///
  /// 与 [I18nMessage] 中的消息键一致，用于关联到特定的国际化消息，不可为空。
  @Column(name = "message_key", nullable = false)
  String messageKey;
  /// 国际化语言标签。
  ///
  /// 表示此翻译文本所属的语言（例如："zh-CN", "en-US"），不可为空。
  @Column(name = "language", nullable = false)
  String language;
  /// 国际化翻译的实际文本内容。
  ///
  /// 特定语言下对应消息键的翻译结果，不可为空。
  @Column(name = "content", length = 4095, nullable = false)
  String content;
}
