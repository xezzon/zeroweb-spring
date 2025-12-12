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

package io.github.xezzon.zeroweb.auth.event;

import io.github.xezzon.zeroweb.auth.RoleUser;
import io.github.xezzon.zeroweb.auth.repository.RoleUserRepository;
import io.github.xezzon.zeroweb.role.constant.RoleConstant;
import io.github.xezzon.zeroweb.user.constant.UserConstant;
import jakarta.annotation.Resource;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/// `RootUserRoleRunner` 是一个应用程序启动器，用于在应用启动时为预定义的超级管理员用户分配根角色。
///
/// 这个组件确保在系统首次启动或特定条件下，超级管理员拥有必要的最高权限。
///
/// @author xezzon
@Slf4j
@Component
@Order(Short.MAX_VALUE)
public class RootUserRoleRunner implements ApplicationRunner {

  @Resource
  private RoleUserRepository roleUserRepository;

  /// 应用程序启动时执行的逻辑。
  ///
  /// 它会检查超级管理员是否已经关联了根角色。如果未关联，则创建该关联。
  ///
  /// @param args 应用程序启动参数。
  @Override
  public void run(@NonNull final ApplicationArguments args) {
    try {
      Optional<RoleUser> root = roleUserRepository.findByRoleIdAndUserId(
          RoleConstant.ROOT.getId(),
          UserConstant.ROOT.getId()
      );
      if (root.isEmpty()) {
        RoleUser roleUser = new RoleUser();
        roleUser.setRoleId(RoleConstant.ROOT.getId());
        roleUser.setUserId(UserConstant.ROOT.getId());
        roleUserRepository.saveAndFlush(roleUser);
      }
    } catch (RuntimeException e) {
      log.warn("Failed to add role for root user.", e);
    }
  }
}
