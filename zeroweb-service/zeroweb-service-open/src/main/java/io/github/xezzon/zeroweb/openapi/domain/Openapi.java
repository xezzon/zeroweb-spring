package io.github.xezzon.zeroweb.openapi.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
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
@Table(name = "zeroweb_openapi")
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
   * 即第三方接口调用的路径
   */
  @Column(name = CODE_COLUMN, nullable = false, unique = true)
  String code;
  /**
   * 后端地址
   * 即该接口应该转发到的后端地址
   */
  @Column(name = "destination", nullable = false, length = 2083)
  @JsonInclude(Include.NON_NULL)
  String destination;
  /**
   * 请求接口的HTTP方法
   */
  @Column(name = "http_method", nullable = false, length = 16)
  @Enumerated(EnumType.STRING)
  HttpMethod httpMethod;
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
