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

package io.github.xezzon.zeroweb.user.converter;

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.user.AddUserReq;
import io.github.xezzon.zeroweb.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// AddUserReq到User的转换器
///
/// 使用MapStruct将注册请求对象转换为用户实体对象。
///
/// @author xezzon
@Mapper
public interface AddUserReqConverter extends From<AddUserReq, User> {

  /// 转换器实例
  AddUserReqConverter INSTANCE = Mappers.getMapper(AddUserReqConverter.class);

  /// 转换为用户实体
  ///
  /// @param req 注册请求对象
  /// @return 用户实体对象
  @Mapping(target = "updateTime", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createTime", ignore = true)
  @Mapping(target = "cipher", ignore = true)
  @Override
  User from(AddUserReq req);
}
