package io.github.xezzon.zeroweb.app.domain;

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
 * 服务
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_zpp")
public class App implements IEntity<String>, II18nMessage {

  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  private String id;
  /**
   * 应用名称
   */
  @Column(name = "name", nullable = false)
  private String name;
  /**
   * 服务基础访问路径
   */
  @Column(name = "base_url", nullable = false, length = 2083)
  private String baseUrl;
  /**
   * 服务顺序 顺序越小越靠前
   */
  @Column(name = "ordinal", nullable = false)
  private Integer ordinal;

  @Override
  public String getNamespace() {
    return "APP_NAME";
  }

  @Override
  public String getMessageKey() {
    return this.name;
  }
}
