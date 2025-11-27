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

package io.github.xezzon.zeroweb.role.entity;

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 新增角色
///
/// @param code 角色简码
/// @param name 角色名称
/// @param inheritable 是否允许该角色新建其下级角色
/// @param parentId 上级角色
/// @author xezzon
public record AddRoleReq(
    String code,
    String name,
    Boolean inheritable,
    String parentId
) implements Into<Role> {

  @Override
  public Role into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddRoleReq, Role> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "value", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Override
    Role from(AddRoleReq source);
  }
}
