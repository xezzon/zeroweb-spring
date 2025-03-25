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
