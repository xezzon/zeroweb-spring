package io.github.xezzon.zeroweb.locale.domain;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 国际化文本
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_i18n_text")
public class I18nText implements IEntity<String> {

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /**
   * 命名空间
   * 与国际化内容的命名空间一致
   */
  @Column(name = "namespace", nullable = false)
  String namespace;
  /**
   * 国际化内容
   */
  @Column(name = "message_key", nullable = false)
  String messageKey;
  /**
   * 国际化语言标签
   */
  @Column(name = "language", nullable = false)
  String language;
  /**
   * 国际化文本
   */
  @Column(name = "content", nullable = false)
  String content;
}
