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

package io.github.xezzon.zeroweb.metadata;

import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/// 权限常量工具类。
/// 用于读取指定 Class 中定义的公共静态字符串字段，并将其转换为 [MenuInfo] 对象，
/// 表示接口权限。
///
/// @author xezzon
public final class PermissionConstantUtil {

  /// 读取指定类中定义的公共静态字符串字段，并将其转换为 [MenuInfo] 列表。
  /// 这些字段的值被视为权限路径，并作为单一权限添加到 [MenuInfo] 中。
  ///
  /// @param constant 包含权限常量的类。
  /// @return [MenuInfo] 对象的列表，每个对象代表一个接口权限。
  /// @throws ZerowebRuntimeException 如果在访问字段时发生 [IllegalAccessException]。
  public static List<MenuInfo> read(Class<?> constant) {
    return Arrays.stream(constant.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> Modifier.isPublic(field.getModifiers()))
        .map(field -> {
          final String value;
          try {
            value = field.get(null).toString();
          } catch (IllegalAccessException e) {
            throw new ZerowebRuntimeException(e);
          }
          final MenuInfo resourceInfo = new MenuInfo();
          resourceInfo.setType(MenuType.PERMISSION);
          resourceInfo.setPath(value);
          resourceInfo.setPermissions(Collections.singleton(value));
          return resourceInfo;
        })
        .toList();
  }

  /// 私有构造函数，防止实例化。
  private PermissionConstantUtil() {
  }
}
