package io.github.xezzon.zeroweb.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
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
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 用户
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_user")
public class User {

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /**
   * 用户名
   */
  @Column(name = "username", nullable = false, unique = true)
  String username;
  /**
   * 用户昵称
   */
  @Column(name = "nickname")
  String nickname;
  /**
   * 密码
   */
  @JsonIgnore
  @Column(name = "cipher", nullable = false)
  String cipher;
  /**
   * 记录创建时间
   */
  @Column(name = "create_time", nullable = false, updatable = false)
  @CreationTimestamp
  Instant createTime;
  /**
   * 最后更新时间
   */
  @Column(name = "update_time", nullable = false)
  @UpdateTimestamp
  Instant updateTime;
}
