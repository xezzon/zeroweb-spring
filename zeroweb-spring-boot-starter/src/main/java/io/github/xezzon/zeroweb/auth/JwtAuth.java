package io.github.xezzon.zeroweb.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.xezzon.zeroweb.auth.entity.JwtClaimWrapper;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * JWT认证相关
 * @author xezzon
 */
public class JwtAuth {

  private final Algorithm algorithm;
  private static final ThreadLocal<JwtClaim> CACHE = new InheritableThreadLocal<>();

  /**
   * 签发JWT时的构造器
   * @param privateKey 私钥
   */
  public JwtAuth(ECPrivateKey privateKey) {
    this.algorithm = Algorithm.ECDSA256(privateKey);
  }

  /**
   * 校验JWT时使用的构造器
   * @param publicKey 公钥
   */
  public JwtAuth(ECPublicKey publicKey) {
    this.algorithm = Algorithm.ECDSA256(publicKey);
  }

  public JwtAuth(byte[] secretKey) {
    this.algorithm = Algorithm.HMAC256(secretKey);
  }

  /**
   * 对JWT进行签名
   * @param jwtBuilder JWT构建器
   * @return JWT
   */
  public String sign(@NotNull JWTCreator.Builder jwtBuilder) {
    return jwtBuilder.sign(algorithm);
  }

  /**
   * 校验、解码JWT令牌
   * @param token 待解码的JWT令牌字符串
   * @return 解码后的JwtClaim对象
   */
  public DecodedJWT decode(String token) {
    JWTVerifier verifier = JWT.require(algorithm).build();
    return verifier.verify(token);
  }

  public static void save(DecodedJWT decodedJWT) {
    JwtClaim claim = JwtClaimWrapper.from(decodedJWT).get();
    CACHE.set(claim);
  }

  public static void clear() {
    CACHE.remove();
  }

  @Nullable
  public static JwtClaim get() {
    return CACHE.get();
  }
}
