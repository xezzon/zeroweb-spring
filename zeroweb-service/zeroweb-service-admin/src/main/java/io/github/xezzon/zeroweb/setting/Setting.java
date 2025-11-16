package io.github.xezzon.zeroweb.setting;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/// 业务参数
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_setting")
@EntityListeners({AuditingEntityListener.class})
public class Setting {

  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  private String id;
  /// 业务参数标识
  @Column(name = "key", nullable = false, updatable = false)
  private String key;
  /// 约束
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "schema", columnDefinition = "json", nullable = false)
  private String schema;
  /// 参数值
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "value", columnDefinition = "json", nullable = false)
  private Map<String, Object> value;
  /// 更新时间
  @Column(name = "update_time", updatable = false)
  private Instant updateTime;
}
