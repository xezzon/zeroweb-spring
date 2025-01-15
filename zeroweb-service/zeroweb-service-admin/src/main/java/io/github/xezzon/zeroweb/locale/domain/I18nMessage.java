package io.github.xezzon.zeroweb.locale.domain;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import io.github.xezzon.zeroweb.locale.event.II18nMessage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 国际化内容
 *
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_i18n_message")
public class I18nMessage implements IEntity<String>, II18nMessage {

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  private String id;
  /**
   * 命名空间
   */
  @Column(name = "namespace", nullable = false)
  private String namespace;
  /**
   * 国际化内容
   */
  @Column(name = "message_key", nullable = false)
  private String messageKey;
}
