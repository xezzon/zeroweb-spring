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

package io.github.xezzon.zeroweb.subscription.entity;

import io.github.xezzon.zeroweb.common.validator.Alphanumeric;
import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.subscription.Subscription;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 添加订阅请求对象，用于向订阅系统申请订阅指定接口
/// @param openapiCode 被订阅的对外接口的编码
/// @param appId 订阅接口的第三方应用标识
/// @author xezzon
public record AddSubscriptionReq(
    @NotNull
    String appId,
    @Alphanumeric(excludes = {Alphanumeric.DOT}) @NotBlank @Size(max = 255)
    String openapiCode
) implements Into<Subscription> {

  /// 将请求对象转换为订阅实体
  /// @return 订阅实体对象
  @Override
  public Subscription into() {
    return Converter.INSTANCE.from(this);
  }

  /// MapStruct 转换器接口，用于将 AddSubscriptionReq 转换为 Subscription
  @Mapper
  interface Converter extends From<AddSubscriptionReq, Subscription> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "openapi", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Override
    Subscription from(AddSubscriptionReq source);
  }
}
