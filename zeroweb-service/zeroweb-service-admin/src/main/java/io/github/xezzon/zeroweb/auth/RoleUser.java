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

package io.github.xezzon.zeroweb.auth;

import static io.github.xezzon.zeroweb.common.constant.DatabaseConstant.ID_LENGTH;

import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// `RoleUser` 实体表示角色与用户之间的关联。
///
/// 它定义了哪些用户属于哪些角色。
///
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_role_user")
public class RoleUser {

  /// 唯一标识符。
  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = ID_LENGTH)
  private String id;
  /// 关联的角色ID。
  @Column(name = "role_id", nullable = false, updatable = false, length = ID_LENGTH)
  private String roleId;
  /// 关联的用户ID。
  @Column(name = "user_id", nullable = false, updatable = false, length = ID_LENGTH)
  private String userId;
}
