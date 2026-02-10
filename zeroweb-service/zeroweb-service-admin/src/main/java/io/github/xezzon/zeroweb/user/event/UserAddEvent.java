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

package io.github.xezzon.zeroweb.user.event;

import io.github.xezzon.zeroweb.user.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/// 用户新增事件
///
/// 继承 Spring ApplicationEvent，用于 Spring 事件机制。
/// @author xezzon
@Getter
public class UserAddEvent extends ApplicationEvent {

  /// 用户实体
  private final User user;

  /// 构造函数
  ///
  /// @param source 事件源
  /// @param user 用户实体
  public UserAddEvent(Object source, User user) {
    super(source);
    this.user = user;
  }
}
