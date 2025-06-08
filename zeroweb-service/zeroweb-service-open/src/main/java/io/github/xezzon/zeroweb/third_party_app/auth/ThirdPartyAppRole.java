package io.github.xezzon.zeroweb.third_party_app.auth;

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
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_third_party_app_role")
public class ThirdPartyAppRole {

  @Id
  @Column(name = "id", unique = true, nullable = false, updatable = false, length = ID_LENGTH)
  @IdGenerator
  private String id;

  @Column(name = "group_id", nullable = false, updatable = false, length = ID_LENGTH)
  private String groupId;

  @Column(name = "name")
  private String name;
}
