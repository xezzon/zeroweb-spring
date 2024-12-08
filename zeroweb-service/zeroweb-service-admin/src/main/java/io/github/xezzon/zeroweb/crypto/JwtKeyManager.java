package io.github.xezzon.zeroweb.crypto;

import com.auth0.jwt.JWTCreator;
import io.github.xezzon.tao.observer.ObserverContext;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig.ZerowebJwtConfig;
import io.github.xezzon.zeroweb.core.crypto.ASN1PublicKeyWriter;
import io.github.xezzon.zeroweb.core.crypto.PemClasspathReaderAndWriter;
import io.github.xezzon.zeroweb.core.crypto.SecretKeyUtil;
import io.github.xezzon.zeroweb.crypto.event.PublicKeyGeneratedEvent;
import io.github.xezzon.zeroweb.crypto.service.JwtCryptoService;
import jakarta.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * JWT 密钥管理器，用于生成、保存和加载 JWT 密钥对以及签发 JWT。
 * @author xezzon
 */
@Component
@Slf4j
public class JwtKeyManager implements JwtCryptoService {

  public static final String ALGORITHM = "EC";
  private final ZerowebJwtConfig zerowebJwtConfig;
  private PrivateKey privateKey;
  private PublicKey publicKey;

  public JwtKeyManager(ZerowebConfig zerowebConfig) {
    this.zerowebJwtConfig = zerowebConfig.getJwt();
    ObserverContext.register(PublicKeyGeneratedEvent.class, this::printPublicKey);
    ObserverContext.register(PublicKeyGeneratedEvent.class, this::savePublicKeyToClasspath);
  }

  /**
   * 在应用启动后，加载私钥。 如果无法找到私钥文件或解析失败，则生成新的密钥对并保存私钥文件。 加载成功后，将公钥广播出去。
   */
  @PostConstruct
  public void loadPrivateKey() {
    PemClasspathReaderAndWriter pemReaderAndWriter =
        new PemClasspathReaderAndWriter(zerowebJwtConfig.getIssuer());
    try {
      /* 从PEM文件中读取公钥、私钥 */
      this.privateKey = SecretKeyUtil.readPrivateKey(pemReaderAndWriter);
      this.publicKey = SecretKeyUtil.readPublicKey(pemReaderAndWriter);
    } catch (Exception e) {
      log.error("Cannot load private key.", e);
    }
    if (this.privateKey == null) {
      /* 获取不到文件或解析不了 则生成一对密钥 */
      KeyPairGenerator keyPairGenerator;
      try {
        keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
      } catch (NoSuchAlgorithmException e) {
        log.error("Cannot create key pair.", e);
        return;
      }
      KeyPair ecc = keyPairGenerator.generateKeyPair();
      this.privateKey = ecc.getPrivate();
      this.publicKey = ecc.getPublic();
      /* 保存私钥 */
      try {
        SecretKeyUtil.writePrivateKey(this.privateKey, pemReaderAndWriter);
      } catch (Exception e) {
        log.error("Cannot save private key to file.", e);
      }
    }
    /* 广播公钥 */
    ObserverContext.post(new PublicKeyGeneratedEvent(publicKey));
  }

  @Override
  public String signJwt(@NotNull JWTCreator.Builder jwtBuilder) {
    Instant iat = Instant.now();
    Instant exp = iat.plusSeconds(zerowebJwtConfig.getTimeout());
    jwtBuilder
        .withIssuer(zerowebJwtConfig.getIssuer())
        .withIssuedAt(iat)
        .withExpiresAt(exp)
        .withJWTId(UUID.randomUUID().toString());
    return new JwtAuth(this.getPrivateKey()).sign(jwtBuilder);
  }

  /**
   * 获取私钥
   * @return 返回ECPrivateKey类型的私钥
   */
  public java.security.interfaces.ECPrivateKey getPrivateKey() {
    return (java.security.interfaces.ECPrivateKey) this.privateKey;
  }

  /**
   * 获取公钥
   * @return 返回ECPublicKey类型的公钥
   */
  public java.security.interfaces.ECPublicKey getPublicKey() {
    return (java.security.interfaces.ECPublicKey) this.publicKey;
  }

  /**
   * 打印公钥到控制台
   * @param event 公钥
   */
  public void printPublicKey(PublicKeyGeneratedEvent event) {
    log.info("Current JWT Public Key is: {}", event.getPublicKey());
  }

  /**
   * 将公钥保存到文件中（PKCS8）
   * @param event 公钥
   */
  public void savePublicKeyToClasspath(PublicKeyGeneratedEvent event) {
    ASN1PublicKeyWriter asn1Writer = new PemClasspathReaderAndWriter(zerowebJwtConfig.getIssuer());
    try {
      SecretKeyUtil.writePublicKey(event.publicKey(), asn1Writer);
    } catch (Exception e) {
      log.error("Cannot save public key to file.", e);
    }
  }
}
