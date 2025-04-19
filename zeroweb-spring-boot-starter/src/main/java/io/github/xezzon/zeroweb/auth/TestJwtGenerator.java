package io.github.xezzon.zeroweb.auth;

import static com.google.auth.http.AuthHttpConstants.BEARER;

import cn.hutool.core.util.RandomUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.xezzon.zeroweb.auth.entity.JwtClaimWrapper;
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

  public static UserBuilder userBuilder() {
    return new UserBuilder();
  }

  public static AppBuilder appBuilder() {
    return new AppBuilder();
  }

  public static class UserBuilder {

    private String userId = UUID.randomUUID().toString();
    private String username = RandomUtil.randomString(8);
    private List<String> roles = Collections.singletonList("test");
    private List<String> permissions = Collections.singletonList("*");

    private UserBuilder() {
    }

    public UserBuilder userId(String userId) {
      this.userId = userId;
      return this;
    }

    public UserBuilder username(String username) {
      this.username = username;
      return this;
    }

    public UserBuilder roles(List<String> roles) {
      this.roles = roles;
      return this;
    }

    public UserBuilder permissions(List<String> permissions) {
      this.permissions = permissions;
      return this;
    }

    public String jwt() {
      return JWT.create()
          .withSubject(userId)
          .withClaim(JwtClaimWrapper.USERNAME_CLAIM, username)
          .withClaim(JwtClaimWrapper.NICKNAME_CLAIM, RandomUtil.randomString(8))
          .withClaim(JwtClaimWrapper.ROLES_CLAIM, roles)
          .withClaim(JwtClaimWrapper.PERMISSION_CLAIM, permissions)
          .withClaim(JwtClaimWrapper.GROUPS_CLAIM, Collections.emptyList())
          .withIssuedAt(Instant.now())
          .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
          .withJWTId(UUID.randomUUID().toString())
          .sign(Algorithm.ECDSA256(PRIVATE_KEY));
    }

    public String bearer() {
      return BEARER + " " + this.jwt();
    }
  }

  public static class AppBuilder {

    private String username = RandomUtil.randomString(8);

    private AppBuilder() {
    }

    public AppBuilder username(String username) {
      this.username = username;
      return this;
    }

    public String jwt() {
      return JWT.create()
          .withSubject(UUID.randomUUID().toString())
          .withClaim(JwtClaimWrapper.USERNAME_CLAIM, username)
          .withClaim(JwtClaimWrapper.NICKNAME_CLAIM, RandomUtil.randomString(8))
          .withClaim(JwtClaimWrapper.ROLES_CLAIM, Collections.singletonList("*"))
          .withClaim(JwtClaimWrapper.PERMISSION_CLAIM, Collections.singletonList("*"))
          .withClaim(JwtClaimWrapper.GROUPS_CLAIM, Collections.emptyList())
          .withIssuedAt(Instant.now())
          .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
          .withJWTId(UUID.randomUUID().toString())
          .sign(Algorithm.HMAC256(SECRET_KEY.getEncoded()));
    }

    public String bearer() {
      return BEARER + " " + this.jwt();
    }
  }
}
