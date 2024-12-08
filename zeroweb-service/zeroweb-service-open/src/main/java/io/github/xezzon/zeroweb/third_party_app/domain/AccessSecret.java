package io.github.xezzon.zeroweb.third_party_app.domain;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Getter;
import lombok.Setter;

/**
 * 第三方应用访问凭据与密钥
 * @author xezzon
 */
@Getter
@Setter
@Entity
@Table(name = ThirdPartyApp.TABLE_NAME)
public class AccessSecret {

  /**
   * 第三方应用标识
   */
  @Id
  @Column(
      name = ThirdPartyApp.ID_COLUMN,
      nullable = false,
      insertable = false,
      updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  String id;
  /**
   * 第三方应用密钥
   */
  @Column(name = "secret_key", nullable = false, length = 64)
  String secretKey;

  /**
   * @return 第三方应用访问凭据
   */
  public String getAccessKey() {
    byte[] accessKey = this.id.getBytes(StandardCharsets.UTF_8);
    return Base64.getEncoder()
        .encodeToString(accessKey);
  }
}
