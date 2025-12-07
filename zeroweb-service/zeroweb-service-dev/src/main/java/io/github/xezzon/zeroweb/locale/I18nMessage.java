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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// 国际化内容实体类
///
/// 用于存储和管理应用程序中的国际化消息。
/// 每个国际化消息由一个唯一的 ID、一个命名空间和一个消息键组成。
///
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_i18n_message")
public class I18nMessage implements IEntity<String>, II18nMessage {

/// 国际化内容的唯一标识符。
///
/// 作为主键，不可为空，不可更新，长度由 [`DatabaseConstant.ID_LENGTH`](zeroweb-service/zeroweb-service-dev/src/main/java/io/github/xezzon/zeroweb/common/constant/DatabaseConstant.java) 定义。
  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  private String id;
/// 国际化内容的命名空间。
///
/// 用于对国际化消息进行逻辑分组，不可为空。
  @Column(name = "namespace", nullable = false)
  private String namespace;
/// 国际化消息的键。
///
/// 在给定的命名空间内唯一标识一个国际化消息，不可为空。
  @Column(name = "message_key", nullable = false)
  private String messageKey;
}
