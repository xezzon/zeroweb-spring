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

package io.github.xezzon.zeroweb.common.constant;

/// 数据库相关常量
/// @author xezzon
public class DatabaseConstant {

  /// 主键字段长度
  public static final int ID_LENGTH = 64;
  /// 默认ID
  /// 用于表示不存在的根节点
  public static final String ROOT_ID = "0";
  /// ID 列名
  public static final String ID_COLUMN = "id";
  /// URL 字段长度
  public static final int URL_LENGTH = 2083;

  /// 私有构造函数，防止实例化
  private DatabaseConstant() {
  }
}
