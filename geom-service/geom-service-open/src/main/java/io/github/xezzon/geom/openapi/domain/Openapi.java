package io.github.xezzon.geom.openapi.domain;

import io.github.xezzon.geom.common.constant.DatabaseConstant;
import io.github.xezzon.geom.common.jpa.IEntity;
import io.github.xezzon.geom.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

  /**
   * 对外接口标识
   */
  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /**
   * 接口编码
   */
  @Column(name = "code", nullable = false)
  String code;
  /**
   * 接口状态
   */
  @Column(name = "status", nullable = false)
  OpenapiStatus status;

  public boolean isPublished() {
    return status == OpenapiStatus.PUBLISHED;
  }
}
