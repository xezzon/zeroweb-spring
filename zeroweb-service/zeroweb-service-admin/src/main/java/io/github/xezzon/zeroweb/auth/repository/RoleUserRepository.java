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

import io.github.xezzon.zeroweb.auth.RoleUser;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// `RoleUserRepository` 是角色-用户关联的 JPA 数据仓库接口。
///
/// 它提供了对 [RoleUser] 实体的数据库操作，包括按角色ID或用户ID查询、检查是否存在、删除以及按角色ID和用户ID查询。
///
@Repository
@NullMarked
public interface RoleUserRepository extends
    JpaRepository<RoleUser, String>,
    JpaSpecificationExecutor<RoleUser> {

  /// 根据角色ID查询所有角色-用户关联。
  ///
  /// @param roleId 角色ID。
  /// @return 匹配的角色-用户关联列表。
  List<RoleUser> findByRoleId(String roleId);

  /// 检查指定角色ID和用户ID的关联是否存在。
  ///
  /// @param roleId 角色ID。
  /// @param userId 用户ID。
  /// @return 如果存在则为 `true`，否则为 `false`。
  boolean existsByRoleIdAndUserId(String roleId, String userId);

  /// 删除指定角色ID和用户ID的角色-用户关联。
  ///
  /// @param roleId 角色ID。
  /// @param userId 用户ID。
  @Transactional
  void deleteByRoleIdAndUserId(String roleId, String userId);

  /// 根据用户ID查询所有角色-用户关联。
  ///
  /// @param userId 用户ID。
  /// @return 匹配的角色-用户关联列表。
  List<RoleUser> findByUserId(String userId);

  /// 根据角色ID和用户ID查询单个角色-用户关联。
  ///
  /// @param roleId 角色ID。
  /// @param userId 用户ID。
  /// @return 匹配的角色-用户关联的 [Optional]，如果不存在则为 [#empty()]。
  Optional<RoleUser> findByRoleIdAndUserId(String roleId, String userId);
}
