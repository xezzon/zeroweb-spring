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

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.core.tree.ITreeService;
import io.github.xezzon.zeroweb.role.IRoleService4Auth;
import io.github.xezzon.zeroweb.role.Role;
import io.github.xezzon.zeroweb.role.RoleConstant;
import io.github.xezzon.zeroweb.role.exception.RoleNotInheritableException;
import io.github.xezzon.zeroweb.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/// 角色服务实现类
///
/// `RoleService` 提供角色管理的业务逻辑实现。
/// 它同时实现了 `ITreeService` 以支持树形结构相关的查询，
/// 以及 `IRoleService4Auth` 为认证模块提供角色相关服务。
///
/// @author xezzon
@Service
public class RoleService implements ITreeService<Role, String>, IRoleService4Auth {

  private final RoleRepository roleRepository;

  /// 依赖注入
  ///
  /// @param roleRepository 角色数据访问层
  public RoleService(final RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  /// 新增角色
  ///
  /// 创建新的角色实例，包含完整的业务逻辑验证和处理：
  /// 1. 验证上级角色是否存在且允许继承
  /// 2. 自动生成角色完整编码路径
  /// 3. 检查角色编码重复性
  /// 4. 持久化到数据库
  ///
  /// @param role 待创建的角色对象，id 和 value 字段会由系统自动生成
  /// @throws RoleNotInheritableException 当上级角色不允许继承时抛出
  /// @throws RepeatDataException 当角色编码重复时抛出
  void addRole(Role role) {
    /* 前置校验校验 */
    // 校验上级角色是否存在并允许继承
    final Optional<Role> parent = roleRepository.findById(role.getParentId());
    if (parent.isEmpty()) {
      throw new RoleNotInheritableException();
    }
    if (Boolean.FALSE.equals(parent.get().getInheritable())) {
      throw new RoleNotInheritableException();
    }
    // 角色编码=上级角色编码+角色简码
    if (RoleConstant.ADMIN.equals(parent.get().getValue())) {
      role.setValue(role.getCode());
    } else {
      role.setValue(parent.get().getValue() + "/" + role.getCode());
    }
    // 校验重复
    final Optional<Role> exist = roleRepository.findByValue(role.getValue());
    if (exist.isPresent()) {
      throw new RepeatDataException(role.getValue());
    }
    /* 持久化到数据库 */
    roleRepository.save(role);
  }

  /// 删除角色
  ///
  /// 根据角色ID删除角色，如果角色不存在则直接返回。
  /// 实际删除操作会调用重载方法处理级联删除。
  ///
  /// @param id 角色ID
  void deleteRole(final String id) {
    final Optional<Role> role = roleRepository.findById(id);
    if (role.isEmpty()) {
      return;
    }
    this.deleteRole(Collections.singleton(role.get()));
  }

  /// 递归删除角色
  ///
  /// 批量删除角色及其所有下级角色。
  /// 采用递归方式确保删除指定角色及其所有子角色。
  /// 操作在事务中执行，确保数据一致性。
  ///
  /// @param roles 待删除的角色集合
  @Transactional
  void deleteRole(final Collection<Role> roles) {
    if (roles.isEmpty()) {
      return;
    }
    final Set<String> roleIds = roles.stream()
        .map(Role::getId)
        .collect(Collectors.toSet());
    roleRepository.deleteAllByIdInBatch(roleIds);
    // 递归删除下级
    final List<Role> children = this.listByParentId(roleIds);
    this.deleteRole(children);
  }

  /// 查询当前用户的角色
  ///
  /// 获取当前登录用户的角色及其下一级角色。
  /// 通过 Sa-Token 获取用户角色列表，然后查询对应的角色对象。
  /// 为每个角色设置其直接子角色，构建角色树结构。
  ///
  /// @return 当前用户的角色列表，包含直接子角色
  List<Role> listMyRole() {
    final List<String> roleValues = StpUtil.getRoleList();
    List<Role> roles = roleRepository.findByValueIn(roleValues);
    final Set<String> roleIds = roles.stream()
        .map(Role::getId)
        .collect(Collectors.toSet());
    final List<Role> children = roleRepository.findByParentIdIn(roleIds);
    for (Role role : roles) {
      role.setChildren(children.stream()
          .filter(child -> Objects.equals(child.getParentId(), role.getId()))
          .toList()
      );
    }
    return roles;
  }

  @Override
  public List<Role> listByParentId(final Collection<String> parentIds) {
    return roleRepository.findByParentIdIn(parentIds);
  }

  @Override
  public List<Role> findByIdIn(final Collection<String> roleIds) {
    return roleRepository.findAllById(roleIds);
  }

  @Override
  public Optional<Role> findParent(final String childId) {
    return roleRepository.findById(childId)
        .flatMap(child ->
            roleRepository.findById(child.getParentId())
        );
  }

  @Override
  public List<Role> topDownList(final Collection<String> initial) {
    return ITreeService.super.topDownList(initial, -1);
  }
}
