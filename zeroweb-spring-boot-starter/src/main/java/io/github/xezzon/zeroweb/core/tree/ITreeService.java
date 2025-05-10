package io.github.xezzon.zeroweb.core.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 树形数据管理
 * @param <T> 树形数据结构
 * @param <I> ID类型
 */
public interface ITreeService<T extends ITreeNode<T, I>, I> {

  /**
   * 查询列表
   * @param parentIds 上级角色ID集合
   * @return 列表
   */
  List<T> listByParentId(Collection<I> parentIds);

  /**
   * 自上而下查询列表
   * @param initial ID集合
   * @param depth 查询深度
   * @return 列表
   */
  default List<T> topDownList(final Collection<I> initial, int depth) {
    if (initial.isEmpty()) {
      return Collections.emptyList();
    }
    List<T> result = new ArrayList<>();
    Collection<I> parentIds = initial;
    while (depth != 0) {
      final List<T> children = this.listByParentId(parentIds)
          .stream()
          .filter(o -> result.stream().noneMatch(r -> Objects.equals(r.getId(), o.getId())))
          .toList();
      if (children.isEmpty()) {
        break;
      }
      result.addAll(children);
      parentIds = children.stream()
          .map(ITreeNode::getId)
          .collect(Collectors.toSet());
      depth--;
    }
    return result;
  }

  /**
   * 自上而下查询列表
   * @param initial ID集合
   * @param depth 查询深度
   * @return 树形列表
   */
  default List<T> topDownTree(final Collection<I> initial, final int depth) {
    final List<T> list = topDownList(initial, depth);
    List<T> root = list.stream()
        .filter(o -> initial.contains(o.getParentId()))
        .toList();
    List<T> nodes = root;
    while (!nodes.isEmpty()) {
      for (T node : nodes) {
        final List<T> children = list.stream()
            .filter(o -> Objects.equals(o.getParentId(), node.getId()))
            .toList();
        node.setChildren(children.isEmpty() ? null : children);
      }
      nodes = nodes.stream()
          .map(ITreeNode::getChildren)
          .filter(Objects::nonNull)
          .flatMap(Collection::stream)
          .toList();
    }
    return root;
  }
}
