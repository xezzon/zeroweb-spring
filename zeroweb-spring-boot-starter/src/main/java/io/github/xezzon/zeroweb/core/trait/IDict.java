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

package io.github.xezzon.zeroweb.core.trait;

/**
 * 字典接口
 * @author xezzon
 */
public interface IDict {

  /**
   * 字典目
   * @return 字典目
   */
  String getTag();

  /**
   * 字典值
   * @return 字典值
   */
  String getCode();

  /**
   * 字典描述
   * @return 字典描述
   */
  String getLabel();

  /**
   * 排序号
   * @return 排序号
   */
  int getOrdinal();
}
