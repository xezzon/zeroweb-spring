/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.xezzon.zeroweb.auth;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.BEARER;

import cn.hutool.core.util.RandomUtil;
import com.fasterxml.uuid.Generators;
import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import io.jsonwebtoken.Jwts.SIG;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.jetbrains.annotations.TestOnly;

/**
 * 为单元测试生成 JWT。
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
      KeyPair keyPair = SIG.ES256.keyPair().build();
      PRIVATE_KEY = (ECPrivateKey) keyPair.getPrivate();
      PUBLIC_KEY = (ECPublicKey) keyPair.getPublic();
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(256);
      SECRET_KEY = SIG.HS256.key().build();
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
        .id(Generators.timeBasedEpochRandomGenerator().generate().toString())
        .username(RandomUtil.randomString(8))
        .roles(Collections.singletonList("test"))
        .permissions(Collections.singletonList("*"));
  }

  public static Builder appBuilder() {
    return new Builder(SECRET_KEY)
        .id(Generators.timeBasedEpochRandomGenerator().generate().toString())
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

    public JwtClaim jwtClaim() {
      return jwtBuilder
          .setNickname(RandomUtil.randomString(8))
          .addAllGroups(Collections.emptyList())
          .build();
    }

    public String jwt() {
      return this.signer
          .issuer("xezzon.github.io")
          .issuedAt(Instant.now())
          .timeout(60 * 60L)
          .sign(this.jwtClaim());
    }

    public String bearer() {
      return BEARER + " " + this.jwt();
    }
  }
}
