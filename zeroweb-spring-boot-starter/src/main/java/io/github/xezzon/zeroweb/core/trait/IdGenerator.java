package io.github.xezzon.zeroweb.core.trait;

/**
 * 全局 ID 生成器
 * @author xezzon
 */
@FunctionalInterface
public interface IdGenerator {

  /**
   * 生成一个全局 ID
   * @return ID
   */
  String nextId();
}
