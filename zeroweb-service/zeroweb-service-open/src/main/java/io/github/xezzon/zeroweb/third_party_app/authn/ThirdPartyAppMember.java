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

/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = ThirdPartyAppMember.TABLE_NAME)
@EntityListeners({AuditingEntityListener.class})
public class ThirdPartyAppMember {

  public static final String TABLE_NAME = "zeroweb_third_party_app_member";
  public static final String GROUP_ID_COLUMN = "group_id";
  public static final String USER_ID_COLUMN = "user_id";
  public static final String ROLE_ID_COLUMN = "role_id";
  public static final String CREATE_TIME_COLUMN = "create_time";

  public static final String DEFAULT_ROLE_ID = "0";
  public static final String OWNER_ROLE_ID = "1";

  @Id
  @IdGenerator
  @Column(name = ID_COLUMN, nullable = false, updatable = false, length = ID_LENGTH)
  private String id;
  @Column(name = GROUP_ID_COLUMN, nullable = false, updatable = false, length = ID_LENGTH)
  private String groupId;
  @Column(name = USER_ID_COLUMN, nullable = false, updatable = false, length = ID_LENGTH)
  private String userId;
  @Column(name = ROLE_ID_COLUMN, nullable = false, length = ID_LENGTH)
  private String roleId;
  @Column(name = CREATE_TIME_COLUMN, nullable = false, updatable = false)
  @CreatedDate
  private Instant createTime;

  public boolean isOwner() {
    return Objects.equals(this.roleId, OWNER_ROLE_ID);
  }

  void moveOwnership(ThirdPartyAppMember member) {
    this.roleId = DEFAULT_ROLE_ID;
    member.roleId = OWNER_ROLE_ID;
  }
}
