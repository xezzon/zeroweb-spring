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

package io.github.xezzon.zeroweb.third_party_app.authn;

import static io.github.xezzon.zeroweb.common.constant.DatabaseConstant.ID_COLUMN;
import static io.github.xezzon.zeroweb.common.constant.DatabaseConstant.ID_LENGTH;

import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/// 第三方应用成员信息
///
/// 表示用户在某个第三方应用中的成员身份，包括角色信息等
///
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = ThirdPartyAppMember.TABLE_NAME)
@EntityListeners({AuditingEntityListener.class})
public class ThirdPartyAppMember {

  /// 数据库表名
  public static final String TABLE_NAME = "zeroweb_third_party_app_member";
  /// 用户组ID列名
  public static final String GROUP_ID_COLUMN = "group_id";
  /// 用户ID列名
  public static final String USER_ID_COLUMN = "user_id";
  /// 角色ID列名
  public static final String ROLE_ID_COLUMN = "role_id";
  /// 创建时间列名
  public static final String CREATE_TIME_COLUMN = "create_time";

  /// 默认角色ID（普通成员）
  public static final String DEFAULT_ROLE_ID = "0";
  /// 所有者角色ID
  public static final String OWNER_ROLE_ID = "1";

  /// 成员标识
  @Id
  @IdGenerator
  @Column(name = ID_COLUMN, nullable = false, updatable = false, length = ID_LENGTH)
  private String id;
  /// 第三方应用ID（作为用户组）
  @Column(name = GROUP_ID_COLUMN, nullable = false, updatable = false, length = ID_LENGTH)
  private String groupId;
  /// 用户ID
  @Column(name = USER_ID_COLUMN, nullable = false, updatable = false, length = ID_LENGTH)
  private String userId;
  /// 角色ID，决定用户在应用中的权限
  @Column(name = ROLE_ID_COLUMN, nullable = false, length = ID_LENGTH)
  private String roleId;
  /// 创建时间
  @Column(name = CREATE_TIME_COLUMN, nullable = false, updatable = false)
  @CreatedDate
  private Instant createTime;

  /// 判断该成员是否是所有者。即
  /// @return [#roleId] == [#OWNER_ROLE_ID] 的结构
  public boolean isOwner() {
    return Objects.equals(this.roleId, OWNER_ROLE_ID);
  }

  /// 将自己设为普通成员，对方设为所有者。
  void moveOwnership(ThirdPartyAppMember member) {
    this.roleId = DEFAULT_ROLE_ID;
    member.roleId = OWNER_ROLE_ID;
  }
}
