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

package io.github.xezzon.zeroweb.openapi.entity;

import io.github.xezzon.zeroweb.common.validator.Alphanumeric;
import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.openapi.Openapi;
import io.github.xezzon.zeroweb.openapi.enumeration.HttpMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 新增对外接口请求对象
///
/// 用于创建新的对外接口，包含接口编码、后端地址和HTTP方法等信息。
/// 该请求对象实现了 [Into] 接口，可以转换为 [Openapi] 实体对象。
///
/// @param code 接口编码，唯一标识一个对外接口
/// @param destination 后端地址，即该接口应该转发到的后端服务地址
/// @param httpMethod 请求接口的HTTP方法，如 GET、POST 等
/// @author xezzon
public record AddOpenapiReq(
    @Alphanumeric(excludes = {Alphanumeric.DOT}) String code,
    String destination,
    HttpMethod httpMethod
) implements Into<Openapi> {

  @Override
  public Openapi into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddOpenapiReq, Openapi> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "id", ignore = true)
    @Override
    Openapi from(AddOpenapiReq source);
  }
}
