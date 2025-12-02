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

@Repository
@NullMarked
public interface RoleUserRepository extends
    JpaRepository<RoleUser, String>,
    JpaSpecificationExecutor<RoleUser> {

  List<RoleUser> findByRoleId(String roleId);

  boolean existsByRoleIdAndUserId(String roleId, String userId);

  @Transactional
  void deleteByRoleIdAndUserId(String roleId, String userId);

  List<RoleUser> findByUserId(String userId);

  Optional<RoleUser> findByRoleIdAndUserId(String roleId, String userId);
}
