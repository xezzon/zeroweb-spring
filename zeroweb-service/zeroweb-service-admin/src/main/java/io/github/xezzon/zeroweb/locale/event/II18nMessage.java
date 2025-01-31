package io.github.xezzon.zeroweb.locale.event;

import jakarta.persistence.MappedSuperclass;

/**
 * 需要国际化的功能，需要实现该接口
 */
@MappedSuperclass
public interface II18nMessage {

  /**
   * @return 命名空间
   */
  String getNamespace();

  /**
   * @return 国际化内容
   */
  String getMessageKey();

  /**
   * 当命名空间与键都相同时，认为是同一国际化内容
   * @param that 另一个实现了国际化内容接口的对象
   * @return 是否认定同一
   */
  default boolean eq(final II18nMessage that) {
    return getNamespace().equals(that.getNamespace())
        && getMessageKey().equals(that.getMessageKey());
  }
}
