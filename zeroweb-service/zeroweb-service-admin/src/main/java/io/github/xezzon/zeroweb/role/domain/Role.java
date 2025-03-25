package io.github.xezzon.zeroweb.role.domain;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IEntity;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import io.github.xezzon.zeroweb.core.tree.ITreeNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 角色
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_role")
public class Role implements IEntity<String>, ITreeNode<Role, String> {

  /**
   * 角色标识
   */
  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  private String id;
  /**
   * 角色简码
   */
  @Column(name = "code", nullable = false)
  private String code;
  /**
   * 角色编码
   */
  @Column(name = "value", nullable = false)
  private String value;
  /**
   * 角色名称
   */
  @Column(name = "name", nullable = false)
  private String name;
  /**
   * 是否允许该角色创建其下级角色
   */
  @Column(name = "inheritable", nullable = false)
  private Boolean inheritable;
  /**
   * 上级角色
   */
  @Column(name = "parent_id", nullable = false)
  private String parentId;
  @Transient
  private List<Role> children;
}
