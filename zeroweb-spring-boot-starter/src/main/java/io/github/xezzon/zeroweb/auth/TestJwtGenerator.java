package io.github.xezzon.zeroweb.auth;

import static com.google.auth.http.AuthHttpConstants.BEARER;

import cn.hutool.core.util.RandomUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.jetbrains.annotations.TestOnly;

/**
 * @author xezzon
 */
@TestOnly
public class TestJwtGenerator {

  private static final Base64.Encoder ENCODER = Base64.getEncoder();
  private static final ECPrivateKey PRIVATE_KEY;
  private static final ECPublicKey PUBLIC_KEY;
  private static final SecretKey SECRET_KEY;

  static {
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
      KeyPair keyPair = keyPairGenerator.generateKeyPair();
      PRIVATE_KEY = (ECPrivateKey) keyPair.getPrivate();
      PUBLIC_KEY = (ECPublicKey) keyPair.getPublic();
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(256);
      SECRET_KEY = keyGenerator.generateKey();
    } catch (NoSuchAlgorithmException e) {
      throw new ZerowebRuntimeException(e);
    }
  }

  private TestJwtGenerator() {
  }

  public static String getPublicKey() {
    return ENCODER.encodeToString(PUBLIC_KEY.getEncoded());
  }

  public static String getSecretKey() {
    return ENCODER.encodeToString(SECRET_KEY.getEncoded());
  }

  public static Builder userBuilder() {
    return new Builder(PRIVATE_KEY)
        .id(UUID.randomUUID().toString())
        .username(RandomUtil.randomString(8))
        .roles(Collections.singletonList("test"))
        .permissions(Collections.singletonList("*"));
  }

  public static Builder appBuilder() {
    return new Builder(SECRET_KEY)
        .id(UUID.randomUUID().toString())
        .username(RandomUtil.randomString(8))
        .roles(Collections.singletonList("*"))
        .permissions(Collections.singletonList("*"));
  }

  public static class Builder {

    private final Algorithm algorithm;
    private final JWTCreator.Builder jwtBuilder = JWT.create();

    private Builder(ECPrivateKey privateKey) {
      this.algorithm = Algorithm.ECDSA256(privateKey);
    }

    private Builder(SecretKey secretKey) {
      this.algorithm = Algorithm.HMAC256(secretKey.getEncoded());
    }

    public Builder id(String id) {
      jwtBuilder.withSubject(id);
      return this;
    }

    public Builder username(String username) {
      jwtBuilder.withClaim(JwtClaimWrapper.USERNAME_CLAIM, username);
      return this;
    }

    public Builder roles(List<String> roles) {
      jwtBuilder.withClaim(JwtClaimWrapper.ROLES_CLAIM, roles);
      return this;
    }

    public Builder permissions(List<String> permissions) {
      jwtBuilder.withClaim(JwtClaimWrapper.PERMISSION_CLAIM, permissions);
      return this;
    }

    public String jwt() {
      return jwtBuilder
          .withClaim(JwtClaimWrapper.NICKNAME_CLAIM, RandomUtil.randomString(8))
          .withClaim(JwtClaimWrapper.GROUPS_CLAIM, Collections.emptyList())
          .withIssuedAt(Instant.now())
          .withClaim(JwtClaimWrapper.TIMEOUT_CLAIM, 60 * 60)
          .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
          .withJWTId(UUID.randomUUID().toString())
          .sign(algorithm);
    }

    public String bearer() {
      return BEARER + " " + this.jwt();
    }
  }
}
