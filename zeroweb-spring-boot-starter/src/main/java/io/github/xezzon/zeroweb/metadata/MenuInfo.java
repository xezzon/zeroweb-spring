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

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// 菜单或资源的信息封装类。
/// 用于描述前端菜单、外部链接、嵌入页面以及后端接口权限和资源权限等。
@Getter
@Setter
@ToString
public class MenuInfo {

  /// 菜单或资源的具体类型，例如路由、外部链接、接口权限等。
  private MenuType type;
  /// 菜单或资源的路径。
  /// 不同类型的资源有不同的路径格式，详见 [MenuType]。
  private String path;
  /// 访问此菜单或资源所需要的权限集合。
  /// 取并集，即访问者必须满足列表中所有权限才能访问。
  private Collection<String> permissions;
}
