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

package io.github.xezzon.zeroweb.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import io.github.xezzon.zeroweb.openapi.enumeration.HttpMethod;
import io.github.xezzon.zeroweb.openapi.enumeration.OpenapiStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// 对外接口
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = Openapi.TABLE_NAME)
public class Openapi implements IEntity<String> {

  /// 表名
  public static final String TABLE_NAME = "zeroweb_openapi";
  /// 接口编码列名
  public static final String CODE_COLUMN = "code";
  /// 后端地址列名
  public static final String DESTINATION_COLUMN = "destination";
  /// HTTP方法列名
  public static final String HTTP_METHOD_COLUMN = "http_method";
  /// 发布状态列名
  public static final String STATUS_COLUMN = "status";

  /// 对外接口标识
  @Id
  @IdGenerator
  @Column(
      name = DatabaseConstant.ID_COLUMN,
      nullable = false,
      updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  String id;
  /// 接口编码
  ///
  /// 即第三方接口调用的路径
  @Column(name = CODE_COLUMN, nullable = false, unique = true)
  String code;
  /// 后端地址
  ///
  /// 即该接口应该转发到的后端地址
  @Column(name = DESTINATION_COLUMN, nullable = false, length = DatabaseConstant.URL_LENGTH)
  @JsonInclude(Include.NON_NULL)
  String destination;
  /// 请求接口的HTTP方法
  @Column(name = HTTP_METHOD_COLUMN, nullable = false, length = 16)
  @Enumerated(EnumType.STRING)
  HttpMethod httpMethod;
  /// 接口状态
  @Column(name = STATUS_COLUMN, nullable = false)
  @Enumerated(EnumType.STRING)
  OpenapiStatus status;

  /// 检查接口是否已发布
  ///
  /// @return 如果接口状态为已发布则返回 true，否则返回 false
  public boolean isPublished() {
    return this.status == OpenapiStatus.PUBLISHED;
  }
}
