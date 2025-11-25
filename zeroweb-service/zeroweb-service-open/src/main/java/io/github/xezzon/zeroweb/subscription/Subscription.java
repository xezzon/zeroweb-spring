package io.github.xezzon.zeroweb.subscription;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import io.github.xezzon.zeroweb.openapi.Openapi;
import io.github.xezzon.zeroweb.subscription.enumeration.SubscriptionStatus;
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

/// 订阅的对外接口
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = Subscription.TABLE_NAME)
public class Subscription implements IEntity<String> {

  public static final String TABLE_NAME = "zeroweb_openapi_subscription";
  public static final String OPENAPI_CODE_COLUMN = "openapi_code";
  public static final String APP_ID_COLUMN = "app_id";
  public static final String STATUS_COLUMN = "status";

  /// 订阅标识
  @Id
  @IdGenerator
  @Column(
      name = DatabaseConstant.ID_COLUMN,
      nullable = false,
      updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  String id;
  /// 第三方应用标识
  @Column(name = APP_ID_COLUMN, nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  String appId;
  /// 对外接口编码
  @Column(name = OPENAPI_CODE_COLUMN, nullable = false, updatable = false)
  String openapiCode;
  /// 订阅状态
  @Column(name = STATUS_COLUMN, nullable = false)
  @Enumerated(EnumType.STRING)
  SubscriptionStatus status;
  /// 对外接口详情
  @ManyToOne
  @JoinColumn(
      name = OPENAPI_CODE_COLUMN,
      referencedColumnName = Openapi.CODE_COLUMN,
      insertable = false,
      updatable = false,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
  )
  Openapi openapi;

  public SubscriptionStatus getSubscriptionStatus() {
    if (this.status == null) {
      return SubscriptionStatus.NONE;
    }
    return this.status;
  }
}
