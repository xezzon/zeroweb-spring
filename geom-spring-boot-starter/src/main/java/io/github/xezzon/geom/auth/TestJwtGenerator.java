package io.github.xezzon.geom.auth;

import static com.google.auth.http.AuthHttpConstants.BEARER;
import static io.github.xezzon.geom.auth.domain.JwtClaimWrapper.GROUPS_CLAIM;
import static io.github.xezzon.geom.auth.domain.JwtClaimWrapper.NICKNAME_CLAIM;
import static io.github.xezzon.geom.auth.domain.JwtClaimWrapper.PERMISSION_CLAIM;
import static io.github.xezzon.geom.auth.domain.JwtClaimWrapper.ROLES_CLAIM;
import static io.github.xezzon.geom.auth.domain.JwtClaimWrapper.USERNAME_CLAIM;

import cn.hutool.core.util.RandomUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.xezzon.geom.common.exception.ErrorCode;
import io.github.xezzon.geom.common.exception.GeomRuntimeException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
import org.jetbrains.annotations.TestOnly;

/**
 * @author xezzon
 */
@TestOnly
public class TestJwtGenerator {

  private static final Base64.Encoder ENCODER = Base64.getEncoder();
  private static final ECPrivateKey PRIVATE_KEY;
  private static final ECPublicKey PUBLIC_KEY;

  static {
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
      KeyPair keyPair = keyPairGenerator.generateKeyPair();
      PRIVATE_KEY = (ECPrivateKey) keyPair.getPrivate();
      PUBLIC_KEY = (ECPublicKey) keyPair.getPublic();
    } catch (NoSuchAlgorithmException e) {
      throw new GeomRuntimeException(ErrorCode.UNKNOWN, e);
    }
  }

  private TestJwtGenerator() {
  }

  public static String generateBearer() {
    return BEARER + " " + generateJwt();
  }

  public static String generateJwt() {
    return JWT.create()
        .withSubject(UUID.randomUUID().toString())
        .withClaim(USERNAME_CLAIM, RandomUtil.randomString(8))
        .withClaim(NICKNAME_CLAIM, RandomUtil.randomString(8))
        .withClaim(ROLES_CLAIM, Collections.emptyList())
        .withClaim(GROUPS_CLAIM, Collections.emptyList())
        .withClaim(PERMISSION_CLAIM, Collections.emptyList())
        .withIssuedAt(Instant.now())
        .withExpiresAt(Instant.now().minus(1, ChronoUnit.HOURS))
        .withJWTId(UUID.randomUUID().toString())
        .sign(Algorithm.ECDSA256(PRIVATE_KEY));
  }

  public static String getPublicKey() {
    return ENCODER.encodeToString(PUBLIC_KEY.getEncoded());
  }
}
