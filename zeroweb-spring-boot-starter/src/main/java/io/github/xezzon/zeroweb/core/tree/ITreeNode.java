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

package io.github.xezzon.zeroweb.core.tree;

import java.util.List;

/**
 * 树形数据类型
 * @param <T> 本身的类型
 * @param <I> ID类型
 */
public interface ITreeNode<T extends ITreeNode<T, I>, I> {

  /**
   * @return ID
   */
  I getId();

  /**
   * @return 上级ID
   */
  I getParentId();

  /**
   * @param children 子级数据集合
   */
  void setChildren(List<T> children);

  /**
   * @return 子级数据集合
   */
  List<T> getChildren();
}
