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

  default boolean equalsTo(II18nMessage o) {
    return getNamespace().equals(o.getNamespace())
        && getMessageKey().equals(o.getMessageKey());
  }
}
