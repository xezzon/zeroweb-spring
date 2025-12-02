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

/// 菜单类型
public enum MenuType {

  /// 路由 路径格式为 `/menu/submenu`
  ROUTE,
  /// 外部链接。点击后会打开一个新的标签页 路径格式为 `https://domain.com/path`
  EXTERNAL_LINK,
  /// 嵌入页面。会在当前页面嵌入一个外部网页。 路径格式为 `https://domain.com/path`
  EMBEDDED,
  /// 接口权限 路径格式为 `resource:operation`，operation 通常为 `read`（可省略）、`write` 等。
  PERMISSION,
  /// 资源权限 路径格式为 `resource:#:operation`，operation 通常为 `read`（可省略）、`write` 等。
  GROUP_PERMISSION,
}
