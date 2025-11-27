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

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.subscription.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// @param openapiCode 被订阅的对外接口的编码
/// @param appId 订阅接口的第三方应用标识
/// @author xezzon
public record AddSubscriptionReq(
    String appId,
    String openapiCode
) implements Into<Subscription> {

  @Override
  public Subscription into() {
    return Converter.INSTANCE.from(this);
  }

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
