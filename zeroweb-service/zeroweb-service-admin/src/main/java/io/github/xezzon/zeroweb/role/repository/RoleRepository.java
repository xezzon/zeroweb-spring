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

package io.github.xezzon.zeroweb.role.repository;

import io.github.xezzon.zeroweb.role.Role;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// 角色数据访问层接口
///
/// 对 [角色][Role] 进行数据库操作的 JPA 接口。
/// 继承 `JpaRepository` 和 `JpaSpecificationExecutor`，
/// 提供基础的CRUD操作和动态查询能力。
/// 同时支持角色树形结构相关的查询操作。
///
/// @author xezzon
@Repository
@NullMarked
public interface RoleRepository extends
    JpaRepository<Role, String>,
    JpaSpecificationExecutor<Role> {

  /// 根据角色编码查询角色
  ///
  /// @param value 角色编码（完整路径，如：ADMIN/SYSTEM/USER）
  /// @return 角色对象（如果存在）
  Optional<Role> findByValue(String value);

  /// 根据父角色ID集合查询子角色
  ///
  /// @param parentIds 父角色ID集合
  /// @return 匹配的子角色列表
  List<Role> findByParentIdIn(Collection<String> parentIds);

  /// 根据角色编码集合批量查询角色
  ///
  /// @param values 角色编码集合
  /// @return 角色列表，按照传入的编码顺序返回
  List<Role> findByValueIn(Collection<String> values);
}
