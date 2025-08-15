package io.github.xezzon.zeroweb.third_party_app.authn;

import static io.github.xezzon.zeroweb.common.constant.DatabaseConstant.ID_LENGTH;

import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

/**
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_third_party_app_member")
public class ThirdPartyAppMember {

  public static final String DEFAULT_ROLE_ID = "0";
  public static final String OWNER_ROLE_ID = "1";

  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = ID_LENGTH)
  private String id;
  @Column(name = "group_id", nullable = false, updatable = false, length = ID_LENGTH)
  private String groupId;
  @Column(name = "user_id", nullable = false, updatable = false, length = ID_LENGTH)
  private String userId;
  @Column(name = "role_id", nullable = false, length = ID_LENGTH)
  private String roleId;
  @Column(name = "create_time", nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createTime;

  public boolean isOwner() {
    return Objects.equals(this.roleId, OWNER_ROLE_ID);
  }

  void moveOwnership(ThirdPartyAppMember member) {
    if (!this.isOwner()) {
      throw new DataPermissionForbiddenException("Current user is not the owner of app.");
    }
    this.roleId = DEFAULT_ROLE_ID;
    member.roleId = OWNER_ROLE_ID;
  }
}
