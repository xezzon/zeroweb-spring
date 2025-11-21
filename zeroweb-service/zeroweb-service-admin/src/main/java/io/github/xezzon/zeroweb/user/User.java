package io.github.xezzon.zeroweb.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/// 用户
///
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_user")
@EntityListeners({AuditingEntityListener.class})
public class User {

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /// 用户名
  ///
  /// 由用户定义的、区别于其他用户的一个标识。
  @Column(name = "username", nullable = false, unique = true)
  String username;
  /// 用户昵称
  ///
  /// 显示在页面上的用户的名称。
  @Column(name = "nickname")
  String nickname;
  /// 密码
  ///
  /// 口令被加密后的内容。
  @JsonIgnore
  @Column(name = "cipher", nullable = false)
  String cipher;
  /// 记录创建时间
  @Column(name = "create_time", nullable = false, updatable = false)
  @CreatedDate
  Instant createTime;
  /// 最后更新时间
  @Column(name = "update_time", nullable = false)
  @LastModifiedDate
  Instant updateTime;
}
