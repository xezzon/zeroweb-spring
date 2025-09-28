package io.github.xezzon.zeroweb.auth;

import static io.github.xezzon.zeroweb.common.constant.DatabaseConstant.ID_LENGTH;

import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// 角色-用户
///
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_role_user")
public class RoleUser {

  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = ID_LENGTH)
  private String id;
  /// 角色ID
  @Column(name = "role_id", nullable = false, updatable = false, length = ID_LENGTH)
  private String roleId;
  /// 用户ID
  @Column(name = "user_id", nullable = false, updatable = false, length = ID_LENGTH)
  private String userId;
}
