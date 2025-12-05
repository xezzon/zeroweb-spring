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

package io.github.xezzon.zeroweb.third_party_app;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/// 第三方应用
///
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = ThirdPartyApp.TABLE_NAME)
@EntityListeners({AuditingEntityListener.class})
public class ThirdPartyApp implements IEntity<String> {

  /// 表名
  public static final String TABLE_NAME = "zeroweb_third_party_app";
  /// 第三方应用名称列名
  public static final String NAME_COLUMN = "name";
  /// 第三方应用所有者列名
  public static final String OWNER_ID_COLUMN = "owner_id";
  /// 应用创建时间列名
  public static final String CREATE_TIME_COLUMN = "create_time";

  /// 第三方应用标识
  @Id
  @Column(
      name = DatabaseConstant.ID_COLUMN,
      nullable = false,
      updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  @IdGenerator
  String id;
  /// 第三方应用名称
  @Column(name = NAME_COLUMN, nullable = false)
  String name;
  /// 第三方应用所有者标识
  @Column(name = OWNER_ID_COLUMN, nullable = false, length = DatabaseConstant.ID_LENGTH)
  String ownerId;
  /// 创建时间
  @Column(name = CREATE_TIME_COLUMN, nullable = false, updatable = false)
  @CreatedDate
  Instant createTime;
}
