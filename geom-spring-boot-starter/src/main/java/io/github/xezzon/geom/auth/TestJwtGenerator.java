package io.github.xezzon.geom.auth;

import static com.google.auth.http.AuthHttpConstants.BEARER;

import cn.hutool.core.util.RandomUtil;
import com.auth0.jwt.JWTCreator.Builder;
import io.github.xezzon.geom.auth.domain.JwtClaimWrapper;
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
    return generateBearer(UUID.randomUUID().toString());
  }

  public static String generateBearer(String userId) {
    return BEARER + " " + generateJwt(userId);
  }

  public static String generateJwt(String userId) {
    JwtClaim claim = JwtClaim.newBuilder()
        .setSubject(userId)
        .setPreferredUsername(RandomUtil.randomString(8))
        .setNickname(RandomUtil.randomString(8))
        .build();
    Builder jwtBuilder = new JwtClaimWrapper(claim)
        .into()
        .withIssuedAt(Instant.now())
        .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
        .withJWTId(UUID.randomUUID().toString());
    return new JwtAuth(PRIVATE_KEY).sign(jwtBuilder);
  }

  public static String getPublicKey() {
    return ENCODER.encodeToString(PUBLIC_KEY.getEncoded());
  }
}
