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

import lombok.Getter;
import lombok.Setter;

/**
 * 服务信息
 * @author xezzon
 */
@Getter
@Setter
public class ServiceInfo {

  /**
   * 服务名称
   */
  private String name;
  /**
   * 服务版本
   */
  private String version;
  /**
   * 服务类型
   */
  private ServiceType type = ServiceType.SERVER;
  /**
   * 是否隐藏 站内的链接不应该包含`隐藏`的服务。
   */
  private boolean hidden = true;
}
