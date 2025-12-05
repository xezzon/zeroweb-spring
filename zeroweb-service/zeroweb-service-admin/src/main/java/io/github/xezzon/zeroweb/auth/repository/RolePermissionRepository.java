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

package io.github.xezzon.zeroweb.auth.repository;

import io.github.xezzon.zeroweb.auth.RolePermission;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// `RolePermissionRepository` 是角色-权限关联的 JPA 数据仓库接口。
///
/// 它提供了对 [RolePermission] 实体的数据库操作，包括按角色ID查询、检查是否存在、删除以及按权限查询。
///
@Repository
@NullMarked
public interface RolePermissionRepository extends
    JpaRepository<RolePermission, String>,
    JpaSpecificationExecutor<RolePermission> {

  /// 根据角色ID集合查询角色-权限关联。
  ///
  /// @param roleIds 角色ID集合。
  /// @return 匹配的角色-权限关联列表。
  List<RolePermission> findByRoleIdIn(Collection<String> roleIds);

  /// 检查指定角色ID和权限编码的关联是否存在。
  ///
  /// @param roleId 角色ID。
  /// @param permission 权限编码。
  /// @return 如果存在则为 `true`，否则为 `false`。
  boolean existsByRoleIdAndPermission(String roleId, String permission);

  /// 删除指定角色ID集合和权限编码的所有角色-权限关联。
  ///
  /// @param roleIds 角色ID集合。
  /// @param permission 权限编码。
  @Transactional
  void deleteByRoleIdInAndPermission(Collection<String> roleIds, String permission);

  /// 根据权限编码查询角色-权限关联。
  ///
  /// @param permission 权限编码。
  /// @return 匹配的角色-权限关联列表。
  List<RolePermission> findByPermission(String permission);
}
