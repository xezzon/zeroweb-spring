package io.github.xezzon.zeroweb.crypto.event;

import java.security.PublicKey;
import java.util.Base64;

/**
 * 公钥生成事件
 * @param publicKey 公钥
 */
public record PublicKeyGeneratedEvent(
    PublicKey publicKey
) {

  public String getPublicKey() {
    return Base64.getEncoder().encodeToString(publicKey.getEncoded());
  }
}
