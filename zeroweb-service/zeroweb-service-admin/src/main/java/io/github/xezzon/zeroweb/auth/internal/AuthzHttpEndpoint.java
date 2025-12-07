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

package io.github.xezzon.zeroweb.auth.internal;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.auth.RolePermission;
import io.github.xezzon.zeroweb.auth.RoleUser;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.role.IRoleService4Auth;
import io.github.xezzon.zeroweb.role.Role;
import io.github.xezzon.zeroweb.user.User;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/// 授权
@RestController
@RequestMapping("/auth")
public class AuthzHttpEndpoint {

  private final AuthzService authzService;
  private final IRoleService4Auth roleService;

  /// 构造函数，注入 [AuthzService] 和 [IRoleService4Auth]。
  ///
  /// @param authzService 授权服务实例。
  /// @param roleService 角色服务接口实例。
  public AuthzHttpEndpoint(final AuthzService authzService, final IRoleService4Auth roleService) {
    this.authzService = authzService;
    this.roleService = roleService;
  }

  /// 查询指定角色绑定的所有用户。
  ///
  /// @param roleId 角色ID。
  /// @return 绑定到该角色的用户信息列表。
  @GetMapping("/role/{roleId}/user")
  public List<User> queryUserByRole(@PathVariable final String roleId) {
    // 当前用户的角色是该角色的上级角色，或者有相应的读取权限
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_READ)) {
      authzService.checkParentRole(roleId);
    }
    return authzService.queryUserByRole(roleId);
  }

  /// 将一个或多个用户绑定到角色。
  ///
  /// @param roleUsers 包含角色ID和用户ID的 [RoleUser] 集合。
  @PutMapping("/role/-/user")
  public void bindUserToRole(@RequestBody Collection<RoleUser> roleUsers) {
    for (RoleUser roleUser : roleUsers) {
      authzService.bindUserToRole(roleUser);
    }
  }

  /// 解除一个或多个用户与角色的关联。
  ///
  /// @param roleUsers 包含角色ID和用户ID的 [RoleUser] 集合。
  @DeleteMapping("/role/-/user")
  public void releaseRoleUser(@RequestBody final Collection<RoleUser> roleUsers) {
    for (final RoleUser roleUser : roleUsers) {
      authzService.releaseRoleUser(roleUser);
    }
  }

  /// 查询指定角色的所有接口权限编码集合。
  ///
  /// @param roleId 角色ID。
  /// @return 包含该角色所有接口权限编码的 [Set]。
  @GetMapping("/role/{roleId}/permission")
  public Set<String> queryPermissionByRole(@PathVariable final String roleId) {
    // 当前用户的角色是该角色或其上级角色，或者有相应的读取权限
    final Role role = roleService.findByIdIn(Collections.singleton(roleId))
        .stream()
        .findAny()
        .orElseThrow();
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_READ)
        && !StpUtil.hasRole(role.getValue())
    ) {
      authzService.checkParentRole(roleId);
    }
    return authzService.queryPermissionByRole(Collections.singleton(roleId));
  }

  /// 角色授予一个或多个接口权限。
  ///
  /// @param rolePermissions 包含角色ID和权限编码的 [RolePermission] 集合。
  @PutMapping("/role/-/permission")
  public void bindPermissionToRole(@RequestBody Collection<RolePermission> rolePermissions) {
    for (RolePermission rolePermission : rolePermissions) {
      authzService.bindPermissionToRole(rolePermission);
    }
  }

  /// 解除一个或多个角色与接口权限的关联。
  ///
  /// @param rolePermissions 包含角色ID和权限编码的 [RolePermission] 集合。
  @DeleteMapping("/role/-/permission")
  public void releaseRolePermission(@RequestBody Collection<RolePermission> rolePermissions) {
    for (RolePermission rolePermission : rolePermissions) {
      authzService.releaseRolePermission(rolePermission);
    }
  }

  /// 查询指定用户关联的所有角色。
  ///
  /// 需要 `AUTHZ_READ` 权限。
  ///
  /// @param userId 用户ID。
  /// @return 关联到该用户的角色信息集合。
  @SaCheckPermission({PermissionConstant.AUTHZ_READ})
  @GetMapping("/user/{userId}/role")
  public List<Role> queryRoleByUser(@PathVariable final String userId) {
    return authzService.queryRoleByUser(userId);
  }

  /// 查询指定接口权限关联的所有角色。
  ///
  /// 需要 `AUTHZ_READ` 权限。
  ///
  /// @param permission 接口权限编码。
  /// @return 关联到该接口权限的角色信息集合。
  @SaCheckPermission({PermissionConstant.AUTHZ_READ})
  @GetMapping("/permission/-/role")
  public List<Role> queryRoleByPermission(@RequestParam final String permission) {
    return authzService.queryRoleByPermission(permission);
  }
}
