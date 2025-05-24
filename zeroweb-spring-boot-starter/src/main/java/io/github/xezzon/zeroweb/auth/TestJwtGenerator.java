package io.github.xezzon.zeroweb.auth;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.BEARER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
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

    private final JsonWebToken.Signer signer;
    private final JwtClaim.Builder jwtBuilder = JwtClaim.newBuilder();

    private Builder(ECPrivateKey privateKey) {
      this.signer = JsonWebToken.signer(privateKey);
    }

    private Builder(SecretKey secretKey) {
      this.signer = JsonWebToken.signer(secretKey.getEncoded());
    }

    public Builder id(String id) {
      jwtBuilder.setSub(id);
      return this;
    }

    public Builder username(String username) {
      jwtBuilder.setPreferredUsername(username);
      return this;
    }

    public Builder roles(List<String> roles) {
      jwtBuilder
          .clearRoles()
          .addAllRoles(roles);
      return this;
    }

    public Builder permissions(List<String> permissions) {
      jwtBuilder
          .clearEntitlements()
          .addAllEntitlements(permissions);
      return this;
    }

    public String jwt() {
      final JwtClaim claim = jwtBuilder
          .setNickname(RandomUtil.randomString(8))
          .addAllGroups(Collections.emptyList())
          .build();
      return this.signer
          .issuer("xezzon.github.io")
          .issuedAt(Instant.now())
          .timeout(60 * 60L)
          .sign(new JwtClaimWrapper(claim));
    }

    public String bearer() {
      return BEARER + " " + this.jwt();
    }
  }
}
