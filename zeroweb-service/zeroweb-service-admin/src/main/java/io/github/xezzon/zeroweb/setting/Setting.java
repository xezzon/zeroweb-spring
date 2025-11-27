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

package io.github.xezzon.zeroweb.setting;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/// 业务参数
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_setting")
@EntityListeners({AuditingEntityListener.class})
public class Setting implements IEntity<String> {

  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  private String id;
  /// 业务参数标识
  @Column(name = "code", nullable = false, updatable = false)
  private String code;
  /// 约束
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "`schema`", columnDefinition = "json", nullable = false)
  private String schema;
  /// 参数值
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "value", columnDefinition = "json", nullable = false)
  private Map<String, Object> value;
  /// 更新时间
  @Column(name = "update_time", nullable = false)
  private Instant updateTime;
}
