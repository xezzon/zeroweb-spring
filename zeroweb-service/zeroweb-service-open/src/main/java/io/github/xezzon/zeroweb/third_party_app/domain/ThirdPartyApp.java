package io.github.xezzon.zeroweb.third_party_app.domain;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

/**
 * 第三方应用
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = ThirdPartyApp.TABLE_NAME)
public class ThirdPartyApp implements IEntity<String> {

  public static final String TABLE_NAME = "zeroweb_third_party_app";
  public static final String ID_COLUMN = "id";

  /**
   * 第三方应用标识
   */
  @Id
  @Column(
      name = ID_COLUMN,
      nullable = false,
      updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  @IdGenerator
  String id;
  /**
   * 第三方应用名称
   */
  @Column(name = "name", nullable = false)
  String name;
  /**
   * 第三方应用所有者标识
   */
  @Column(name = "owner_id", nullable = false, length = DatabaseConstant.ID_LENGTH)
  String ownerId;
  /**
   * 创建时间
   */
  @Column(name = "create_time", nullable = false, updatable = false)
  @CreationTimestamp
  Instant createTime;
}
