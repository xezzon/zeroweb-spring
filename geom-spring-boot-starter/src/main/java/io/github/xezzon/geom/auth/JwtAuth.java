package io.github.xezzon.geom.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.github.xezzon.geom.auth.JwtClaim.Builder;
import io.github.xezzon.geom.auth.domain.JwtClaimWrapper;
import io.github.xezzon.geom.common.exception.InvalidSessionException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import org.jetbrains.annotations.NotNull;

/**
 * JWT认证相关
 * @author xezzon
 */
public class JwtAuth {

  public static final String CLAIM_NAME = "claim";

  private final Algorithm algorithm;

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

  /**
   * @return 用户认证信息
   */
  public static JwtClaim loadJwtClaim() {
    Builder builder = JwtClaim.newBuilder();
    String json = StpUtil.getSession()
        .getString(CLAIM_NAME);
    try {
      JsonFormat.parser().merge(json, builder);
      return builder.build();
    } catch (InvalidProtocolBufferException e) {
      throw new InvalidSessionException(e);
    }
  }

  /**
   * @param claim 用户认证信息
   */
  public static void saveJwtClaim(JwtClaim claim) {
    try {
      StpUtil.getSession()
          .set(CLAIM_NAME, JsonFormat.printer().print(claim));
    } catch (InvalidProtocolBufferException e) {
      StpUtil.logout();
      throw new InvalidSessionException(e);
    }
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
  public JwtClaim decode(String token) {
    JWTVerifier verifier = JWT.require(algorithm).build();
    DecodedJWT jwt = verifier.verify(token);
    return JwtClaimWrapper.from(jwt).get();
  }
}
