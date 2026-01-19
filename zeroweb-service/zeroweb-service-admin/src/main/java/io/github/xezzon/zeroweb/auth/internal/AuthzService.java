/*
 * SPDX-FileCopyrightText: Copyright (C) 2025-2026 xezzon
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

import static io.github.xezzon.zeroweb.role.RoleConstant.ADMIN_ID;
import static io.github.xezzon.zeroweb.role.RoleConstant.SUPER_ID;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import io.github.xezzon.zeroweb.auth.RolePermission;
import io.github.xezzon.zeroweb.auth.RoleUser;
import io.github.xezzon.zeroweb.auth.event.UserLoginEvent;
import io.github.xezzon.zeroweb.auth.repository.RolePermissionRepository;
import io.github.xezzon.zeroweb.auth.repository.RoleUserRepository;
import io.github.xezzon.zeroweb.auth.util.SessionUtil;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.role.IRoleService4Auth;
import io.github.xezzon.zeroweb.role.Role;
import io.github.xezzon.zeroweb.user.IUserService4Auth;
import io.github.xezzon.zeroweb.user.User;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/// `AuthzService` 是授权服务的核心业务逻辑处理组件。
///
/// 它负责管理角色与用户、角色与权限之间的关系，提供绑定、解绑、查询等操作，
/// 并在用户登录后将授权信息加载到会话中。
///
/// @author xezzon
@Service
public class AuthzService {

  private final RoleUserRepository roleUserRepository;
  private final RolePermissionRepository rolePermissionRepository;
  private final IUserService4Auth userService;
  private final IRoleService4Auth roleService;

  /// 构造函数，注入授权服务所需的所有数据仓库和服务。
  ///
  /// @param roleUserRepository 角色用户数据仓库。
  /// @param rolePermissionRepository 角色权限数据仓库。
  /// @param userService 用户服务接口。
  /// @param roleService 角色服务接口。
  public AuthzService(
      final RoleUserRepository roleUserRepository,
      final RolePermissionRepository rolePermissionRepository,
      final IUserService4Auth userService,
      final IRoleService4Auth roleService
  ) {
    this.roleUserRepository = roleUserRepository;
    this.rolePermissionRepository = rolePermissionRepository;
    this.userService = userService;
    this.roleService = roleService;
  }

  /// 查询指定角色绑定的所有用户。
  ///
  /// @param roleId 角色ID。
  /// @return 绑定到该角色的用户信息列表。
  List<User> queryUserByRole(final String roleId) {
    final List<RoleUser> roleUsers = roleUserRepository.findByRoleId(roleId);
    final Set<String> userIds = roleUsers.stream()
        .map(RoleUser::getUserId)
        .collect(Collectors.toSet());
    return userService.findByIdIn(userIds);
  }

  /// 将用户绑定到指定角色。
  ///
  /// 在绑定前会检查当前用户是否具有绑定权限或是否是该角色的上级角色。
  /// 如果用户-角色关系已存在，则不做处理。
  ///
  /// @param roleUser 包含用户ID和角色ID的 [RoleUser] 对象。
  @Transactional
  public void bindUserToRole(RoleUser roleUser) {
    final String roleId = roleUser.getRoleId();
    final String userId = roleUser.getUserId();
    // 当前用户的角色是该角色的上级角色，或者有对应写入权限
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_ROLE_USER)) {
      this.checkParentRole(roleId);
    }
    if (roleUserRepository.existsByRoleIdAndUserId(roleId, userId)) {
      return;
    }
    roleUserRepository.save(roleUser);
  }

  /// 解除用户与角色的关联。
  ///
  /// 在解绑前会检查当前用户是否具有解绑权限、是否是该角色的上级角色或是否为被操作的用户自身。
  ///
  /// @param roleUser 包含用户ID和角色ID的 [RoleUser] 对象。
  @Transactional
  public void releaseRoleUser(RoleUser roleUser) {
    final String roleId = roleUser.getRoleId();
    final String userId = roleUser.getUserId();
    // 当前用户的角色是该角色的上级角色，或者有对应写入权限，或者用户是自己
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_ROLE_USER)
        && !Objects.equals(StpUtil.getLoginId(), userId)
    ) {
      this.checkParentRole(roleId);
    }
    roleUserRepository.deleteByRoleIdAndUserId(roleId, userId);
  }

  /// 查询指定用户关联的所有角色。
  ///
  /// @param userId 用户ID。
  /// @return 关联到该用户的角色信息集合。
  List<Role> queryRoleByUser(final String userId) {
    final List<RoleUser> roleUsers = roleUserRepository.findByUserId(userId);
    final Set<String> roleIds = roleUsers.stream()
        .map(RoleUser::getRoleId)
        .collect(Collectors.toSet());
    return roleService.findByIdIn(roleIds);
  }

  /// 批量查询指定角色ID集合关联的接口权限。
  ///
  /// @param roleIds 角色ID集合。
  /// @return 包含所有关联接口权限编码的 [Set]。
  Set<String> queryPermissionByRole(final Collection<String> roleIds) {
    final List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIdIn(roleIds);
    return rolePermissions.stream()
        .map(RolePermission::getPermission)
        .collect(Collectors.toSet());
  }

  /// 将接口权限绑定到指定角色。
  ///
  /// 在绑定前会检查当前用户是否具有绑定权限，以及该权限是否超出其上级角色的权限范围。
  /// 如果角色-权限关系已存在，则不做处理。
  ///
  /// @param rolePermission 包含角色ID和权限编码的 [RolePermission] 对象。
  /// @throws NotPermissionException 如果尝试授予的权限超出了上级角色的权限范围。
  @Transactional
  public void bindPermissionToRole(RolePermission rolePermission) {
    final String roleId = rolePermission.getRoleId();
    final String permission = rolePermission.getPermission();
    // 当前用户是该角色的上级角色，或者有对应的写入权限
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_ROLE_PERMISSION)) {
      this.checkParentRole(roleId);
    }
    if (!Set.of(ADMIN_ID, SUPER_ID).contains(roleId)) {
      // 角色的权限不能超过其上级角色。（内置角色没有上级，不参与校验）
      final Role parent = roleService.findParent(roleId).orElseThrow();
      final List<String> parentPermissions = rolePermissionRepository
          .findByRoleIdIn(Collections.singleton(parent.getId()))
          .stream()
          .map(RolePermission::getPermission)
          .distinct()
          .toList();
      if (Boolean.FALSE.equals(
          SaStrategy.instance.hasElement.apply(parentPermissions, permission)
      )) {
        throw new NotPermissionException(permission);
      }
    }
    if (rolePermissionRepository.existsByRoleIdAndPermission(roleId, permission)) {
      return;
    }
    rolePermissionRepository.save(rolePermission);
  }

  /// 解除角色与接口权限的关联。
  ///
  /// 在解绑前会检查当前用户是否具有解绑权限或是否是该角色的上级角色。
  /// 同时会解除该角色及其所有子角色与该权限的关联。
  ///
  /// @param rolePermission 包含角色ID和权限编码的 [RolePermission] 对象。
  @Transactional
  public void releaseRolePermission(RolePermission rolePermission) {
    final String roleId = rolePermission.getRoleId();
    final String permission = rolePermission.getPermission();
    // 当前用户是该角色的上级角色，或者有对应的写入权限
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_ROLE_PERMISSION)) {
      this.checkParentRole(roleId);
    }
    final List<Role> roles = roleService.topDownList(Collections.singleton(roleId));
    List<String> roleIds = roles.stream()
        .map(Role::getId)
        .collect(Collectors.toList());
    roleIds.add(roleId);
    rolePermissionRepository.deleteByRoleIdInAndPermission(roleIds, permission);
  }

  /// 查询指定接口权限关联的所有角色。
  ///
  /// @param permission 接口权限编码。
  /// @return 关联到该接口权限的角色信息集合。
  List<Role> queryRoleByPermission(final String permission) {
    final List<RolePermission> rolePermissions = rolePermissionRepository.findByPermission(
        permission);
    final Set<String> roleIds = rolePermissions.stream()
        .map(RolePermission::getRoleId)
        .collect(Collectors.toSet());
    return roleService.findByIdIn(roleIds);
  }

  /// 监听用户登录事件，将用户的角色和权限信息加载到会话中。
  ///
  /// @param event 用户登录事件 [UserLoginEvent]。
  @EventListener
  void listen(final UserLoginEvent event) {
    final List<Role> roles = this.queryRoleByUser(event.getUser().getId());
    final Set<String> roleValues = roles.stream()
        .map(Role::getValue)
        .collect(Collectors.toSet());
    SessionUtil.saveRoles(roleValues);
    final Set<String> roleIds = roles.stream()
        .map(Role::getId)
        .collect(Collectors.toSet());
    final Set<String> permissions = this.queryPermissionByRole(roleIds);
    SessionUtil.savePermissions(permissions);
  }

  /// 校验当前用户是否有指定角色的上级角色。
  ///
  /// 如果当前用户不拥有指定角色的上级角色，则会抛出权限异常。
  ///
  /// @param roleId 角色ID。
  void checkParentRole(final String roleId) {
    final Role parent = roleService.findParent(roleId).orElseThrow();
    StpUtil.checkRole(parent.getValue());
  }
}
