package io.github.xezzon.geom.auth.domain;

import cn.dev33.satoken.stp.StpUtil;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.github.xezzon.geom.auth.JwtClaim;
import io.github.xezzon.geom.auth.JwtClaim.Builder;
import io.github.xezzon.geom.common.exception.InvalidSessionException;
import java.security.interfaces.ECPrivateKey;
import org.jetbrains.annotations.NotNull;

/**
 * JWT认证相关
 * @author xezzon
 */
public class JwtAuth {

  public static final String CLAIM_NAME = "claim";

  private final Algorithm algorithm;

  public JwtAuth(ECPrivateKey privateKey) {
    this.algorithm = Algorithm.ECDSA256(privateKey);
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
}
