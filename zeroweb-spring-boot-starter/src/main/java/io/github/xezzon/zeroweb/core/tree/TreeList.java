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

import io.github.xezzon.zeroweb.core.trait.Into;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/// 表示一个树形结构，其中节点是 [ITreeNode]。
/// 此类提供了将扁平节点列表转换为树形结构以及将树形结构扁平化为列表的方法。
///
/// @param <T> 树节点的类型，必须继承 [ITreeNode]。
/// @author xezzon
public class TreeList<T extends ITreeNode<T, ?>> extends AbstractList<T> implements Into<List<T>> {

  private final List<T> root;

  /// 使用给定的根节点构造一个新的 `TreeList`。
  ///
  /// @param root 根节点列表。
  public TreeList(final List<T> root) {
    this.root = root;
  }

  /// 将 [ITreeNode] 对象的扁平列表转换为树形结构。
  /// 树的构建基于 `parentId` 和 `id` 建立父子关系。
  ///
  /// @param list 要转换为树的扁平节点列表。
  /// @param <T> 树节点的类型，必须继承 [ITreeNode]。
  /// @return 表示构建的树形结构的 `TreeList`。
  public static <T extends ITreeNode<T, ?>> TreeList<T> from(List<T> list) {
    List<T> root = top(list);
    TreeList<T> tree = new TreeList<>(root);
    List<T> parents = root;
    Map<?, List<T>> childrenMap = list.stream()
        .collect(Collectors.groupingBy(ITreeNode::getParentId));
    while (!parents.isEmpty()) {
      for (T parent : parents) {
        List<T> children = childrenMap.getOrDefault(parent.getId(), Collections.emptyList());
        parent.setChildren(children);
      }
      parents = parents.stream()
          .map(ITreeNode::getChildren)
          .flatMap(List::stream)
          .toList();
    }
    return tree;
  }

  /// 从 [ITreeNode] 对象的扁平列表中识别并返回顶层（根）节点。
  /// 如果节点的 `parentId` 在列表中不存在对应的 `id`，则该节点被视为顶层节点。
  ///
  /// @param list 扁平节点列表。
  /// @param <T> 树节点的类型，必须继承 [ITreeNode]。
  /// @return 顶层节点列表。
  public static <T extends ITreeNode<T, ?>> List<T> top(List<T> list) {
    Set<?> ids = list.stream()
        .map(ITreeNode::getId)
        .collect(Collectors.toSet());
    return list.stream()
        .filter(node -> !ids.contains(node.getParentId()))
        .toList();
  }

  /// 返回此树的根列表中指定位置的节点。
  ///
  /// @param index 要返回的节点的索引。
  /// @return 指定索引处的节点。
  /// @throws IndexOutOfBoundsException 如果索引超出范围 (`index < 0 || index >= size()`)。
  @Override
  public T get(final int index) {
    return root.get(index);
  }

  /// 返回此树中的根节点数量。
  ///
  /// @return 此树中的根节点数量。
  @Override
  public int size() {
    return root.size();
  }

  /// 将此树结构转换为所有节点的扁平列表，并保持遍历顺序。
  /// 节点以广度优先的方式添加到列表中。
  ///
  /// @return 包含树中所有节点的扁平列表。
  @Override
  public List<T> into() {
    List<T> list = new ArrayList<>(this.size());
    List<T> nodes = root;
    while (!nodes.isEmpty()) {
      list.addAll(nodes);
      nodes = nodes.stream()
          .map(ITreeNode::getChildren)
          .flatMap(List::stream)
          .toList();
    }
    return list;
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof TreeList<?> treeList)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(root, treeList.root);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), root);
  }
}
