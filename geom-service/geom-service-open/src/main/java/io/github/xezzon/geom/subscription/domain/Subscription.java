package io.github.xezzon.geom.subscription.domain;

import io.github.xezzon.geom.common.constant.DatabaseConstant;
import io.github.xezzon.geom.common.jpa.IEntity;
import io.github.xezzon.geom.common.jpa.IdGenerator;
import io.github.xezzon.geom.openapi.domain.Openapi;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订阅的对外接口
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "geom_openapi_subscription")
public class Subscription implements IEntity<String> {

  public static final String OPENAPI_CODE_COLUMN = "openapi_code";

  /**
   * 订阅标识
   */
  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /**
   * 第三方应用标识
   */
  @Column(name = "app_id", nullable = false, updatable = false)
  String appId;
  /**
   * 对外接口编码
   */
  @Column(name = OPENAPI_CODE_COLUMN, nullable = false, updatable = false)
  String openapiCode;
  /**
   * 订阅状态
   */
  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  OpenapiSubscriptionStatus status;
  /**
   * 订阅的接口详情
   */
  @ManyToOne
  @JoinColumn(
      name = OPENAPI_CODE_COLUMN,
      referencedColumnName = Openapi.CODE_COLUMN,
      insertable = false,
      updatable = false,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
  )
  Openapi openapi;
}
