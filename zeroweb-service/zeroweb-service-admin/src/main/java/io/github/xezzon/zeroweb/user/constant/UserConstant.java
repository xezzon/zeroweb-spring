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

package io.github.xezzon.zeroweb.user.constant;

import io.github.xezzon.zeroweb.user.User;

/// 用户常量类
///
/// 定义用户管理相关的常量值，包括默认用户等。
///
/// @author xezzon
public class UserConstant {

  /// 系统初始用户（超级管理员）
  public static final User ROOT = new User();

  static {
    ROOT.setUsername("root");
    ROOT.setNickname("超级管理员");
  }

  private UserConstant() {
  }
}
