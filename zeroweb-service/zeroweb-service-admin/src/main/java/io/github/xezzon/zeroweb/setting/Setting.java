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
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/// 业务参数实体类
///
/// 用于存储和管理系统中各种业务参数，包括系统配置、业务规则等。
/// 支持JSON格式的参数值和参数约束定义。
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_setting")
@EntityListeners({AuditingEntityListener.class})
public class Setting implements IEntity<String> {

  private static final ObjectMapper JACKSON = new ObjectMapper();

  /// 主键ID，唯一标识
  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  private String id;
  /// 业务参数标识
  ///
  /// 唯一标识参数类型的编码，如 `system.theme`、`business.timeout` 等
  /// 不可更新，创建时确定
  @Column(name = "code", nullable = false, updatable = false)
  private String code;
  /// 参数约束定义
  ///
  /// JSON格式的约束配置，定义参数的格式、取值范围、验证规则等。
  ///
  /// @see <a href="https://json-schema.org/specification">JSON Schema</a>
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "definition", columnDefinition = "json", nullable = false)
  private String schema;
  /// 参数实际值
  ///
  /// JSON格式的参数值，支持复杂数据结构
  /// 如 `{"theme": "dark", "language": "zh-CN"}`
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "value", columnDefinition = "json", nullable = false)
  private Map<String, Object> value;
  /// 参数更新时间
  @Column(name = "update_time", nullable = false)
  private Instant updateTime;

  /// 将参数值转换为指定的类型。
  ///
  /// 慎用！可能会有性能问题。
  ///
  /// @param <T> 转换的类型
  /// @param type 转换的类型
  /// @return 转换后的对象
  public <T> T convertValueTo(TypeReference<T> type) {
    return JACKSON.convertValue(this.value, type);
  }
}
