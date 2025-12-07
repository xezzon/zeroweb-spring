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

/// 服务元数据信息封装类。
/// 包含服务的名称、版本、类型以及是否隐藏等信息，用于服务自省。
///
/// @author xezzon
@Getter
@Setter
public class ServiceInfo {

  /// 服务的名称，例如 "user-service" 或 "product-service"。
  private String name;
  /// 服务的版本，例如 "1.0.0"。
  private String version;
  /// 服务的类型，例如 [ServiceType#CLIENT] (前端) 或 [ServiceType#SERVER] (后端)。
  /// 默认为 [ServiceType#SERVER]。
  private ServiceType type = ServiceType.SERVER;
  /// 指示服务是否应该在站内链接中隐藏。
  /// 如果为 `true`，则通常不应在公共菜单或服务列表中显示。
  /// 默认为 `true`。
  private boolean hidden = true;
}
