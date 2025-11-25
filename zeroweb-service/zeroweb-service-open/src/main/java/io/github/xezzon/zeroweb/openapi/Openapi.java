package io.github.xezzon.zeroweb.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import io.github.xezzon.zeroweb.openapi.enumeration.HttpMethod;
import io.github.xezzon.zeroweb.openapi.enumeration.OpenapiStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// 对外接口
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = Openapi.TABLE_NAME)
public class Openapi implements IEntity<String> {

  public static final String TABLE_NAME = "zeroweb_openapi";
  public static final String CODE_COLUMN = "code";
  public static final String DESTINATION_COLUMN = "destination";
  public static final String HTTP_METHOD_COLUMN = "http_method";
  public static final String STATUS_COLUMN = "status";

  /// 对外接口标识
  @Id
  @IdGenerator
  @Column(
      name = DatabaseConstant.ID_COLUMN,
      nullable = false,
      updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  String id;
  /// 接口编码
  ///
  /// 即第三方接口调用的路径
  @Column(name = CODE_COLUMN, nullable = false, unique = true)
  String code;
  /// 后端地址
  ///
  /// 即该接口应该转发到的后端地址
  @Column(name = DESTINATION_COLUMN, nullable = false, length = 2083)
  @JsonInclude(Include.NON_NULL)
  String destination;
  /// 请求接口的HTTP方法
  @Column(name = HTTP_METHOD_COLUMN, nullable = false, length = 16)
  @Enumerated(EnumType.STRING)
  HttpMethod httpMethod;
  /// 接口状态
  @Column(name = STATUS_COLUMN, nullable = false)
  @Enumerated(EnumType.STRING)
  OpenapiStatus status;

  public boolean isPublished() {
    return this.status == OpenapiStatus.PUBLISHED;
  }
}
