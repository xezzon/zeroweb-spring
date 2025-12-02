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

@Repository
@NullMarked
public interface RolePermissionRepository extends
    JpaRepository<RolePermission, String>,
    JpaSpecificationExecutor<RolePermission> {

  List<RolePermission> findByRoleIdIn(Collection<String> roleIds);

  boolean existsByRoleIdAndPermission(String roleId, String permission);

  @Transactional
  void deleteByRoleIdInAndPermission(Collection<String> roleIds, String permission);

  List<RolePermission> findByPermission(String permission);
}
