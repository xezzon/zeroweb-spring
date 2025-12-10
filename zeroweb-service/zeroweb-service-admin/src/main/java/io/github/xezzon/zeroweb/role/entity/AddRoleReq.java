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

import io.github.xezzon.zeroweb.common.validator.Alphanumeric;
import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.role.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 新增角色请求对象
///
/// 用于封装新增角色时客户端提交的请求参数。
/// 该类实现了 [Into] 接口，支持转换为[角色实体][Role]对象。
/// 使用 MapStruct 进行对象转换，提供类型安全的映射功能。
///
/// @param code 角色简码，用于区分同级别角色
/// @param name 角色名称，用于界面展示
/// @param inheritable 是否允许创建下级角色
/// @param parentId 上级角色ID
///
/// @author xezzon
public record AddRoleReq(
    @Alphanumeric @NotBlank @Size(max = 255)
    String code,
    @NotBlank @Size(max = 255)
    String name,
    @NotNull
    Boolean inheritable,
    @NotNull
    String parentId
) implements Into<Role> {

  @Override
  public Role into() {
    return Converter.INSTANCE.from(this);
  }

  /// 对象转换器
  ///
  /// 使用 MapStruct 提供的类型安全的对象映射功能，
  /// 将 `AddRoleReq` 对象转换为 `Role` 实体对象。
  /// 转换时忽略 value、children、id 字段，这些字段由业务逻辑自动生成。
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
