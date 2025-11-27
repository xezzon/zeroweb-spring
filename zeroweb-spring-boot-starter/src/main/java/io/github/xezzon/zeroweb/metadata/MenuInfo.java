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

/**
 * 资源信息
 */
@Getter
@Setter
@ToString
public class MenuInfo {

  /**
   * 资源类型
   */
  private MenuType type;
  /**
   * 资源路径 不同类型的资源有不同的路径格式
   * @see MenuType
   */
  private String path;
  /**
   * 访问资源所需要的权限 取并集，即资源必须满足所列出的所有权限
   */
  private Collection<String> permissions;
}
