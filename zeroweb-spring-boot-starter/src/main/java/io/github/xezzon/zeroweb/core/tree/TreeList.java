package io.github.xezzon.zeroweb.core.tree;

import io.github.xezzon.zeroweb.core.trait.Into;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 树形结构
 * @author xezzon
 */
public class TreeList<T extends ITreeNode<T, ?>> extends AbstractList<T> implements Into<List<T>> {

  private final List<T> root;

  public TreeList(final List<T> root) {
    this.root = root;
  }

  public static <T extends ITreeNode<T, ?>> TreeList<T> from(List<T> list) {
    List<T> root = top(list);
    TreeList<T> tree = new TreeList<>(root);
    List<T> parents = root;
    while (!parents.isEmpty()) {
      for (T parent : parents) {
        List<T> children = list.stream()
            .filter(node -> Objects.equals(node.getParentId(), parent.getId()))
            .toList();
        parent.setChildren(children);
      }
      parents = parents.stream()
          .map(ITreeNode::getChildren)
          .flatMap(List::stream)
          .toList();
    }
    return tree;
  }

  public static <T extends ITreeNode<T, ?>> List<T> top(List<T> list) {
    Set<?> ids = list.stream()
        .map(ITreeNode::getId)
        .collect(Collectors.toSet());
    return list.stream()
        .filter(node -> !ids.contains(node.getParentId()))
        .toList();
  }

  @Override
  public T get(final int index) {
    return root.get(index);
  }

  @Override
  public int size() {
    return root.size();
  }

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
}
