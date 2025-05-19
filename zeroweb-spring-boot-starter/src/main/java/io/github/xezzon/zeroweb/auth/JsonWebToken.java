package io.github.xezzon.zeroweb.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * 签发/解码JWT
 * @author xezzon
 */
public class JsonWebToken {

  private JsonWebToken() {
  }

  /**
   * 签发 JWT （私钥签名）
   * @param privateKey ECC私钥
   * @return JWT签发器
   */
  public static Signer signer(ECPrivateKey privateKey) {
    return new Signer(Algorithm.ECDSA256(privateKey));
  }

  /**
   * 签发 JWT （对称密钥签名）
   * @param secretKey 密钥
   * @return JWT 签发器
   */
  public static Signer signer(byte[] secretKey) {
    return new Signer(Algorithm.HMAC256(secretKey));
  }

  /**
   * 验签、解码 JWT （公钥验签）
   * @param publicKey 公钥
   * @return JWT 解码器
   */
  public static Decoder decoder(ECPublicKey publicKey) {
    return new Decoder(Algorithm.ECDSA256(publicKey));
  }

  /**
   * 验签、解码 JWT（对称密钥验签）
   * @param secretKey 密钥
   * @return JWT 解码器
   */
  public static Decoder decoder(byte[] secretKey) {
    return new Decoder(Algorithm.HMAC256(secretKey));
  }

  /**
   * JWT 签发器
   */
  public static class Signer {

    /**
     * 签名算法及密钥
     */
    private final Algorithm algorithm;
    /**
     * JWT签发者
     */
    private String issuer;
    /**
     * JWT签发时间
     */
    private Instant issuedAt = Instant.now();
    /**
     * JWT过期时间
     */
    private Long timeout;

    private Signer(Algorithm algorithm) {
      this.algorithm = algorithm;
    }

    /**
     * @param issuer JWT签发者
     */
    public Signer issuer(String issuer) {
      this.issuer = issuer;
      return this;
    }

    /**
     * @param issuedAt JWT签发时间
     */
    public Signer issuedAt(@NotNull Instant issuedAt) {
      this.issuedAt = issuedAt;
      return this;
    }

    /**
     * @param timeout JWT有效期，单位（秒）
     */
    public Signer timeout(Long timeout) {
      this.timeout = timeout;
      return this;
    }

    /**
     * @return JWT过期时间
     */
    private Instant expiresAt() {
      if (this.issuedAt != null && this.timeout != null) {
        return this.issuedAt.plusSeconds(timeout);
      }
      return null;
    }

    /**
     * 签发JWT
     * @param claimWrapper JWT自定义载荷内容
     * @return JWT字符串
     */
    public String sign(JwtClaimWrapper claimWrapper) {
      return claimWrapper.jwtBuilder()
          .withIssuer(issuer)
          .withIssuedAt(issuedAt)
          .withClaim(JwtClaimWrapper.TIMEOUT_CLAIM, timeout)
          .withExpiresAt(this.expiresAt())
          .withJWTId(UUID.randomUUID().toString())
          .sign(algorithm);
    }
  }

  /**
   * JWT解码器
   */
  public static class Decoder {

    /**
     * 验签算法及密钥
     */
    private final Algorithm algorithm;

    public Decoder(Algorithm algorithm) {
      this.algorithm = algorithm;
    }

    /**
     * 验签并解码
     * @param token JWT字符串
     * @return JWT对象
     */
    JwtClaimWrapper decode(String token) {
      JWTVerifier verifier = JWT.require(algorithm).build();
      return new JwtClaimWrapper(verifier.verify(token));
    }
  }
}
