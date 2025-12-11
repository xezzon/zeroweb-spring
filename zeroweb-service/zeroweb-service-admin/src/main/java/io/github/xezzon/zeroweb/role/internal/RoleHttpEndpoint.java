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

package io.github.xezzon.zeroweb.role.internal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.role.Role;
import io.github.xezzon.zeroweb.role.entity.AddRoleReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 角色管理
///
/// @author xezzon
@RestController
@RequestMapping("/role")
public class RoleHttpEndpoint {

  private final RoleService roleService;

  /// 依赖注入
  ///
  /// @param roleService 角色服务实例
  public RoleHttpEndpoint(final RoleService roleService) {
    this.roleService = roleService;
  }

  /// 新增角色
  ///
  /// 创建新的角色实例，需要提供角色基本信息。
  /// 角色编码会根据父角色自动生成完整路径。
  ///
  /// @param req 角色信息请求对象，包含简码、名称、是否可继承、上级角色ID
  /// @return 新创建角色的ID
  @SaCheckPermission({PermissionConstant.ROLE_WRITE})
  @PostMapping()
  public Id addRole(@RequestBody @Valid final AddRoleReq req) {
    final Role role = req.into();
    roleService.addRole(role);
    return Id.of(role.getId());
  }

  /// 查询角色列表
  ///
  /// 获取系统所有角色的树形结构列表。
  /// 从系统根节点开始，返回完整的角色树。
  ///
  /// @return 角色列表（树形结构）
  @SaCheckPermission({PermissionConstant.ROLE_READ})
  @GetMapping()
  public List<Role> listAllRole() {
    return roleService.topDownTree(
        Collections.singleton(DatabaseConstant.ROOT_ID),
        -1
    );
  }

  /// 删除角色
  ///
  /// 删除指定的角色及其所有下级角色。
  /// 删除操作会级联删除该角色的所有子角色。
  ///
  /// @param id 角色ID
  @SaCheckPermission({PermissionConstant.ROLE_WRITE})
  @DeleteMapping("/{id}")
  public void deleteRole(@PathVariable @NotBlank final String id) {
    roleService.deleteRole(id);
  }

  /// 查询当前登录人的角色
  ///
  /// 获取当前登录用户的角色及其下一级角色。
  /// 返回当前用户直接拥有的角色，以及这些角色的直接子角色。
  ///
  /// @return 当前用户的角色列表
  @SaCheckLogin
  @GetMapping("/mine")
  public List<Role> listMyRole() {
    return roleService.listMyRole();
  }
}
