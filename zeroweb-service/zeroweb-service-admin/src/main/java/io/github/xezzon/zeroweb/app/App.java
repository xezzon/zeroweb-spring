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

package io.github.xezzon.zeroweb.app;

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

/// 服务实体类，对应数据库中的 `zeroweb_app` 表。
///
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_app")
public class App implements IEntity<String> {

  /// 服务ID，唯一标识一个服务。
  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  private String id;
  /// 应用名称，例如 "管理服务" 或 "开放服务"。
  @Column(name = "name", nullable = false)
  private String name;
  /// 服务的基础访问路径，通常是一个 URL。
  @Column(name = "base_url", nullable = false, length = DatabaseConstant.URL_LENGTH)
  private String baseUrl;
  /// 服务的显示顺序。
  ///
  /// 值越小表示优先级越高，在界面上会越靠前显示。
  @Column(name = "ordinal", nullable = false)
  private Integer ordinal;
}
