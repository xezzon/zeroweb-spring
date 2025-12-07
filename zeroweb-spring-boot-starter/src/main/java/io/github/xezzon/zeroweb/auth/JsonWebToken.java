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

import com.fasterxml.uuid.Generators;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.SignatureAlgorithm;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.jspecify.annotations.NonNull;

/// 签发/解码 JWT 的工具类。
/// 提供了基于不同加密算法（ECC、HMAC）的 JWT 签发器和解码器。
/// @author xezzon
public final class JsonWebToken {

  /// JWT 中用于存储用户名的声明（Claim）名称。
  public static final String USERNAME_CLAIM = "preferred_username";
  /// JWT 中用于存储用户昵称的声明（Claim）名称。
  public static final String NICKNAME_CLAIM = "nickname";
  /// JWT 中用于存储用户角色的声明（Claim）名称。
  public static final String ROLES_CLAIM = "roles";
  /// JWT 中用于存储用户组的声明（Claim）名称。
  public static final String GROUPS_CLAIM = "groups";
  /// JWT 中用于存储用户权限的声明（Claim）名称。
  public static final String PERMISSION_CLAIM = "entitlements";
  /// JWT 中用于存储授权方（Authorized Party）的声明（Claim）名称，通常指 ID 令牌的发行方。
  public static final String AUTHORIZED_PARTY_CLAIM = "azp";
  /// JWT 授权方的值，表示由 ZeroWeb 签发。
  public static final String AZP_VALUE = "zeroweb";
  /// JWT 中用于存储令牌有效时长的声明（Claim）名称，单位为秒。
  public static final String TIMEOUT_CLAIM = "exi";
  /// JWT 的默认过期时间，单位为秒（2分钟）。
  public static final Long DEFAULT_TIMEOUT = 2 * 60L;

  private JsonWebToken() {
  }

  /// 创建一个 JWT 签发器，使用 ECC 私钥进行签名。
  /// @param privateKey 用于签名的 ECC 私钥。
  /// @return 配置好的 JWT 签发器。
  public static Signer signer(final ECPrivateKey privateKey) {
    return new EcdsaSigner(privateKey);
  }

  /// 创建一个 JWT 签发器，使用对称密钥进行签名。
  /// @param secretKey 用于签名的对称密钥。
  /// @return 配置好的 JWT 签发器。
  public static Signer signer(final byte[] secretKey) {
    return new HmacSigner(Keys.hmacShaKeyFor(secretKey));
  }

  /// 创建一个 JWT 解码器，使用 ECC 公钥进行验签。
  /// @param publicKey 用于验签的 ECC 公钥。
  /// @return 配置好的 JWT 解码器。
  public static Decoder decoder(final ECPublicKey publicKey) {
    return new EcdsaDecoder(publicKey);
  }

  /// 创建一个 JWT 解码器，使用对称密钥进行验签。
  /// @param secretKey 用于验签的对称密钥。
  /// @return 配置好的 JWT 解码器。
  public static Decoder decoder(final byte[] secretKey) {
    return new HmacDecoder(Keys.hmacShaKeyFor(secretKey));
  }

  /// JWT 签发器的抽象基类，提供了配置 JWT 声明和签发过程的通用方法。
  public abstract static class Signer {

    /// JWT 的签发者。
    private String issuer;
    /// JWT 的签发时间，默认为当前时间。
    private Instant issuedAt = Instant.now();
    /// JWT 的有效期限，单位为秒，默认为 `DEFAULT_TIMEOUT`。
    private Long timeout = DEFAULT_TIMEOUT;

    /// 设置 JWT 的签发者。
    /// @param issuer JWT 签发者的字符串标识。
    /// @return 当前签发器实例，支持链式调用。
    public Signer issuer(final String issuer) {
      this.issuer = issuer;
      return this;
    }

    /// 设置 JWT 的签发时间。
    /// @param issuedAt JWT 的签发时间。
    /// @return 当前签发器实例，支持链式调用。
    public Signer issuedAt(@NonNull final Instant issuedAt) {
      this.issuedAt = issuedAt;
      return this;
    }

    /// 设置 JWT 的有效期限。
    /// @param timeout JWT 的有效期限，单位为秒。
    /// @return 当前签发器实例，支持链式调用。
    public Signer timeout(@NonNull final Long timeout) {
      this.timeout = timeout;
      return this;
    }

    /// 构建 JWT 的载荷（Payload）。
    /// @param claim 包含自定义 JWT 声明的 `JwtClaim` 对象。
    /// @return 包含已设置载荷的 `JwtBuilder` 实例。
    protected JwtBuilder payload(final JwtClaim claim) {
      return Jwts.builder()
          .subject(claim.getSub())
          .claim(USERNAME_CLAIM, claim.getPreferredUsername())
          .claim(NICKNAME_CLAIM, claim.getNickname())
          .claim(ROLES_CLAIM, claim.getRolesList())
          .claim(GROUPS_CLAIM, Collections.emptyList())
          .claim(PERMISSION_CLAIM, claim.getEntitlementsList())
          .claim(AUTHORIZED_PARTY_CLAIM, AZP_VALUE)
          .claim(TIMEOUT_CLAIM, this.timeout)
          .issuer(this.issuer)
          .issuedAt(Date.from(this.issuedAt))
          .expiration(Date.from(this.expiresAt()))
          .id(Generators.timeBasedEpochRandomGenerator().generate().toString())
          ;
    }

    /// 计算 JWT 的过期时间。
    /// @return JWT 的过期时间。
    private Instant expiresAt() {
      return this.issuedAt.plusSeconds(timeout);
    }

    /// 抽象方法：签发 JWT。
    /// @param claim 包含自定义 JWT 声明的 `JwtClaim` 对象。
    /// @return 签发后的 JWT 字符串。
    public abstract String sign(final JwtClaim claim);
  }

  /// 使用 ECDSA 算法签发 JWT 的具体实现。
  static class EcdsaSigner extends Signer {

    /// ECDSA 签名算法，此处固定为 ES256。
    private final SignatureAlgorithm algorithm = SIG.ES256;
    /// 用于 ECDSA 签名的私钥。
    private final PrivateKey key;

    /// 构造函数，初始化 ECDSA 签发器。
    /// @param key 用于签名的私钥。
    private EcdsaSigner(PrivateKey key) {
      this.key = key;
    }

    /// 使用 ECDSA 算法签发 JWT。
    /// @param claim 包含自定义 JWT 声明的 `JwtClaim` 对象。
    /// @return 签发后的 JWT 字符串。
    @Override
    public String sign(final JwtClaim claim) {
      return this.payload(claim).signWith(key, algorithm).compact();
    }
  }

  /// 使用 HMAC 算法签发 JWT 的具体实现。
  static class HmacSigner extends Signer {

    /// HMAC 签名算法，此处固定为 HS256。
    private final MacAlgorithm algorithm = SIG.HS256;
    /// 用于 HMAC 签名的密钥。
    private final SecretKey key;

    /// 构造函数，初始化 HMAC 签发器。
    /// @param key 用于签名的密钥。
    HmacSigner(SecretKey key) {
      this.key = key;
    }

    /// 使用 HMAC 算法签发 JWT。
    /// @param claim 包含自定义 JWT 声明的 `JwtClaim` 对象。
    /// @return 签发后的 JWT 字符串。
    @Override
    public String sign(JwtClaim claim) {
      return this.payload(claim).signWith(key, algorithm).compact();
    }
  }

  /// JWT 解码器的抽象基类，提供了验签和解码 JWT 的通用方法。
  public abstract static class Decoder {

    /// 验签并解码 JWT 字符串。
    /// @param token 需要验签和解码的 JWT 字符串。
    /// @return 包含 JWT 自定义载荷内容的 `JwtClaim` 对象。
    @SuppressWarnings("unchecked")
    JwtClaim decode(final String token) {
      Claims payload = this.parser().parseSignedClaims(token).getPayload();
      return JwtClaim.newBuilder()
          .setSub(payload.getSubject())
          .setPreferredUsername(payload.get(USERNAME_CLAIM, String.class))
          .setNickname(payload.get(NICKNAME_CLAIM, String.class))
          .addAllRoles(payload.get(ROLES_CLAIM, List.class))
          .addAllEntitlements(payload.get(PERMISSION_CLAIM, List.class))
          .addAllGroups(payload.get(GROUPS_CLAIM, List.class))
          .setExi(payload.get(TIMEOUT_CLAIM, Long.class))
          .build();
    }

    /// 抽象方法：获取 JWT 解析器。
    /// @return 配置好的 `JwtParser` 实例。
    protected abstract JwtParser parser();
  }

  /// 使用 ECDSA 算法验签并解码 JWT 的具体实现。
  static class EcdsaDecoder extends Decoder {

    /// 用于 ECDSA 验签的公钥。
    private final PublicKey key;

    /// 构造函数，初始化 ECDSA 解码器。
    /// @param key 用于验签的公钥。
    private EcdsaDecoder(final PublicKey key) {
      this.key = key;
    }

    /// 获取 ECDSA JWT 解析器。
    /// @return 配置好的 `JwtParser` 实例。
    @Override
    protected JwtParser parser() {
      return Jwts.parser().verifyWith(key).build();
    }
  }

  /// 使用 HMAC 算法验签并解码 JWT 的具体实现。
  static class HmacDecoder extends Decoder {

    /// 用于 HMAC 验签的密钥。
    private final SecretKey key;

    /// 构造函数，初始化 HMAC 解码器。
    /// @param key 用于验签的密钥。
    HmacDecoder(SecretKey key) {
      this.key = key;
    }

    /// 获取 HMAC JWT 解析器。
    /// @return 配置好的 `JwtParser` 实例。
    @Override
    protected JwtParser parser() {
      return Jwts.parser().verifyWith(key).build();
    }
  }
}
