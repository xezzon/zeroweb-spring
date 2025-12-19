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

package io.github.xezzon.zeroweb.auth.util;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.user.User;
import java.util.HashSet;
import java.util.Set;

/// `SessionUtil` 是一个工具类，提供方便的方法来管理 Sa-Token 会话中的用户、角色和权限信息。
///
/// 所有方法都是静态的，可以直接通过类名调用。
///
/// @author xezzon
public final class SessionUtil {

  /// Session 中存储用户信息的键。
  public static final String USER = "user";
  /// Session 中存储角色信息的键。
  public static final String ROLE = "roles";
  /// Session 中存储权限信息的键。
  public static final String PERMISSION = "permissions";

  /// 私有构造函数，防止实例化工具类。
  private SessionUtil() {
  }

  /// 将用户对象保存到当前 Sa-Token 会话中。
  ///
  /// @param user 要保存的 [User] 对象。
  public static void saveUser(final User user) {
    StpUtil.getSession().set(USER, user);
  }

  /// 从当前 Sa-Token 会话中加载用户对象。
  ///
  /// @return Session 中存储的 [User] 对象。
  public static User loadUser() {
    return StpUtil.getSession().getModel(USER, User.class);
  }

  /// 将角色集合保存到当前 Sa-Token 会话中。
  ///
  /// @param roles 要保存的角色字符串集合。
  public static void saveRoles(final Set<String> roles) {
    StpUtil.getSession().set(ROLE, new RoleSet(roles));
  }

  /// 从当前 Sa-Token 会话中加载角色集合。
  ///
  /// @return Session 中存储的角色字符串集合。
  public static Set<String> loadRoles() {
    return StpUtil.getSession().getModel(ROLE, RoleSet.class);
  }

  /// 将权限集合保存到当前 Sa-Token 会话中。
  ///
  /// @param permissions 要保存的权限字符串集合。
  public static void savePermissions(final Set<String> permissions) {
    StpUtil.getSession().set(PERMISSION, new PermissionSet(permissions));
  }

  /// 从当前 Sa-Token 会话中加载权限集合。
  ///
  /// @return Session 中存储的权限字符串集合。
  public static Set<String> loadPermissions() {
    return StpUtil.getSession().getModel(PERMISSION, PermissionSet.class);
  }
}

/// `RoleSet` 是一个继承自 [HashSet] 的内部类，用于在 Session 中存储角色集合。
class RoleSet extends HashSet<String> {

  /// 构造函数，用于反序列化。
  @SuppressWarnings("unused")
  RoleSet() {
    super();
  }

  /// 构造函数，使用给定的角色集合初始化。
  ///
  /// @param roles 角色字符串集合。
  RoleSet(final Set<String> roles) {
    super(roles);
  }
}

/// `PermissionSet` 是一个继承自 [HashSet] 的内部类，用于在 Session 中存储权限集合。
class PermissionSet extends HashSet<String> {

  /// 构造函数，用于反序列化。
  @SuppressWarnings("unused")
  PermissionSet() {
    super();
  }

  /// 构造函数，使用给定的权限集合初始化。
  ///
  /// @param permissions 权限字符串集合。
  PermissionSet(final Set<String> permissions) {
    super(permissions);
  }
}
