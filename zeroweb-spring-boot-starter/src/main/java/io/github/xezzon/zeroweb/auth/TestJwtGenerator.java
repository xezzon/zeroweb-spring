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
 * 为单元测试生成 JWT (JSON Web Token) 的实用工具类。
 * 该类提供静态方法来生成用于测试目的的公钥、私钥和对称密钥，并包含一个 Builder 类用于方便地构造具有特定声明的 JWT。
 *
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

  /// 获取 Base64 编码的公钥字符串。
  /// @return Base64 编码的公钥字符串。
  public static String getPublicKey() {
    return ENCODER.encodeToString(PUBLIC_KEY.getEncoded());
  }

  /// 获取 Base64 编码的对称密钥字符串。
  /// @return Base64 编码的对称密钥字符串。
  public static String getSecretKey() {
    return ENCODER.encodeToString(SECRET_KEY.getEncoded());
  }

  /// 创建一个用于生成用户 JWT 的 Builder 实例。
  /// 默认情况下，会设置一个随机 ID、随机用户名、"test" 角色和所有权限 ("*")。
  /// @return TestJwtGenerator.Builder 实例。
  public static Builder userBuilder() {
    return new Builder(PRIVATE_KEY)
        .id(Generators.timeBasedEpochRandomGenerator().generate().toString())
        .username(RandomUtil.randomString(8))
        .roles(Collections.singletonList("test"))
        .permissions(Collections.singletonList("*"));
  }

  /// 创建一个用于生成应用程序 JWT 的 Builder 实例。
  /// 默认情况下，会设置一个随机 ID、随机用户名、所有角色 ("*") 和所有权限 ("*")。
  /// @return TestJwtGenerator.Builder 实例。
  public static Builder appBuilder() {
    return new Builder(SECRET_KEY)
        .id(Generators.timeBasedEpochRandomGenerator().generate().toString())
        .username(RandomUtil.randomString(8))
        .roles(Collections.singletonList("*"))
        .permissions(Collections.singletonList("*"));
  }

  /// TestJwtGenerator 的 Builder 类，用于构造具有特定声明的 JWT。
  /// 提供链式方法来设置 JWT 的各个字段。
  public static class Builder {

    private final JsonWebToken.Signer signer;
    private final JwtClaim.Builder jwtBuilder = JwtClaim.newBuilder();

    /// 使用 EC 私钥创建一个 Builder 实例。
    /// @param privateKey 用于 JWT 签名的 EC 私钥。
    private Builder(ECPrivateKey privateKey) {
      this.signer = JsonWebToken.signer(privateKey);
    }

    /// 使用对称密钥创建一个 Builder 实例。
    /// @param secretKey 用于 JWT 签名的对称密钥。
    private Builder(SecretKey secretKey) {
      this.signer = JsonWebToken.signer(secretKey.getEncoded());
    }

    /// 设置 JWT 的 ID (sub 声明)。
    /// @param id JWT 的唯一标识符。
    /// @return 当前 Builder 实例。
    public Builder id(String id) {
      jwtBuilder.setSub(id);
      return this;
    }

    /// 设置 JWT 的用户名 (preferred_username 声明)。
    /// @param username 用户的首选用户名。
    /// @return 当前 Builder 实例。
    public Builder username(String username) {
      jwtBuilder.setPreferredUsername(username);
      return this;
    }

    /// 设置 JWT 的角色列表 (roles 声明)。
    /// @param roles 用户的角色列表。
    /// @return 当前 Builder 实例。
    public Builder roles(List<String> roles) {
      jwtBuilder
          .clearRoles()
          .addAllRoles(roles);
      return this;
    }

    /// 设置 JWT 的权限列表 (entitlements 声明)。
    /// @param permissions 用户的权限列表。
    /// @return 当前 Builder 实例。
    public Builder permissions(List<String> permissions) {
      jwtBuilder
          .clearEntitlements()
          .addAllEntitlements(permissions);
      return this;
    }

    /// 构建并返回 JwtClaim 对象。
    /// 会设置一个随机昵称和空的用户组列表。
    /// @return 包含所有设置声明的 JwtClaim 对象。
    public JwtClaim jwtClaim() {
      return jwtBuilder
          .setNickname(RandomUtil.randomString(8))
          .addAllGroups(Collections.emptyList())
          .build();
    }

    /// 生成签名的 JWT 字符串。
    /// JWT 会被签发者 "xezzon.github.io" 签发，并设置当前时间为签发时间，超时时间为 1 小时。
    /// @return 签名的 JWT 字符串。
    public String jwt() {
      return this.signer
          .issuer("xezzon.github.io")
          .issuedAt(Instant.now())
          .timeout(60 * 60L)
          .sign(this.jwtClaim());
    }

    /// 生成带有 "Bearer" 前缀的完整授权头字符串。
    /// @return 格式为 "Bearer <JWT>" 的授权头字符串。
    public String bearer() {
      return BEARER + " " + this.jwt();
    }
  }
}
