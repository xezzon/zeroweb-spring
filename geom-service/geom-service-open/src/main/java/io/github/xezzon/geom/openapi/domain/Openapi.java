package io.github.xezzon.geom.openapi.domain;

import io.github.xezzon.geom.common.constant.DatabaseConstant;
import io.github.xezzon.geom.common.jpa.IEntity;
import io.github.xezzon.geom.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 对外接口
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "geom_openapi")
public class Openapi implements IEntity<String> {

  public static final String ID_COLUMN = "id";
  public static final String CODE_COLUMN = "code";

  /**
   * 对外接口标识
   */
  @Id
  @Column(name = ID_COLUMN, nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /**
   * 接口编码
   */
  @Column(name = CODE_COLUMN, nullable = false, unique = true)
  String code;
  /**
   * 接口状态
   */
  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  OpenapiStatus status;

  public boolean isPublished() {
    return this.status == OpenapiStatus.PUBLISHED;
  }
}
