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

/**
 * 角色-接口权限
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_role_permission")
public class RolePermission {

  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = ID_LENGTH)
  private String id;
  /**
   * 角色ID
   */
  @Column(name = "role_id", nullable = false, updatable = false, length = ID_LENGTH)
  private String roleId;
  /**
   * 接口权限编码
   */
  @Column(name = "permission", nullable = false, updatable = false)
  private String permission;
}
