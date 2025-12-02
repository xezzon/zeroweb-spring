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

/**
 * 签发/解码JWT
 * @author xezzon
 */
public final class JsonWebToken {

  /**
   * 用户名
   */
  public static final String USERNAME_CLAIM = "preferred_username";
  /**
   * 昵称
   */
  public static final String NICKNAME_CLAIM = "nickname";
  /**
   * 角色
   */
  public static final String ROLES_CLAIM = "roles";
  /**
   * 用户组
   */
  public static final String GROUPS_CLAIM = "groups";
  /**
   * 权限
   */
  public static final String PERMISSION_CLAIM = "entitlements";
  /**
   * 授权方 - ID 令牌的发行方
   */
  public static final String AUTHORIZED_PARTY_CLAIM = "azp";
  /**
   * 授权方： ZeroWeb
   */
  public static final String AZP_VALUE = "zeroweb";
  /**
   * 令牌有效时长（秒）
   */
  public static final String TIMEOUT_CLAIM = "exi";
  /**
   * JWT默认过期时间（秒）
   */
  public static final Long DEFAULT_TIMEOUT = 2 * 60L;

  private JsonWebToken() {
  }

  /**
   * 签发 JWT （私钥签名）
   * @param privateKey ECC私钥
   * @return JWT签发器
   */
  public static Signer signer(final ECPrivateKey privateKey) {
    return new EcdsaSigner(privateKey);
  }

  /**
   * 签发 JWT （对称密钥签名）
   * @param secretKey 密钥
   * @return JWT 签发器
   */
  public static Signer signer(final byte[] secretKey) {
    return new HmacSigner(Keys.hmacShaKeyFor(secretKey));
  }

  /**
   * 验签、解码 JWT （公钥验签）
   * @param publicKey 公钥
   * @return JWT 解码器
   */
  public static Decoder decoder(final ECPublicKey publicKey) {
    return new EcdsaDecoder(publicKey);
  }

  /**
   * 验签、解码 JWT（对称密钥验签）
   * @param secretKey 密钥
   * @return JWT 解码器
   */
  public static Decoder decoder(final byte[] secretKey) {
    return new HmacDecoder(Keys.hmacShaKeyFor(secretKey));
  }

  /**
   * JWT 签发器
   */
  public abstract static class Signer {

    /**
     * JWT签发者
     */
    private String issuer;
    /**
     * JWT签发时间
     */
    private Instant issuedAt = Instant.now();
    /**
     * JWT过期时间
     */
    private Long timeout = DEFAULT_TIMEOUT;

    /**
     * @param issuer JWT签发者
     */
    public Signer issuer(final String issuer) {
      this.issuer = issuer;
      return this;
    }

    /**
     * @param issuedAt JWT签发时间
     */
    public Signer issuedAt(@NonNull final Instant issuedAt) {
      this.issuedAt = issuedAt;
      return this;
    }

    /**
     * @param timeout JWT有效期，单位（秒）
     */
    public Signer timeout(@NonNull final Long timeout) {
      this.timeout = timeout;
      return this;
    }

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

    /**
     * @return JWT过期时间
     */
    private Instant expiresAt() {
      return this.issuedAt.plusSeconds(timeout);
    }

    /**
     * 签发JWT
     * @param claim JWT自定义载荷内容
     * @return JWT字符串
     */
    public abstract String sign(final JwtClaim claim);
  }

  static class EcdsaSigner extends Signer {

    /**
     * 签名算法
     */
    private final SignatureAlgorithm algorithm = SIG.ES256;
    /**
     * 签名密钥
     */
    private final PrivateKey key;

    private EcdsaSigner(PrivateKey key) {
      this.key = key;
    }

    @Override
    public String sign(final JwtClaim claim) {
      return this.payload(claim).signWith(key, algorithm).compact();
    }
  }

  static class HmacSigner extends Signer {

    /**
     * 签名算法
     */
    private final MacAlgorithm algorithm = SIG.HS256;
    /**
     * 签名密钥
     */
    private final SecretKey key;

    HmacSigner(SecretKey key) {
      this.key = key;
    }

    @Override
    public String sign(JwtClaim claim) {
      return this.payload(claim).signWith(key, algorithm).compact();
    }
  }

  /**
   * JWT解码器
   */
  public abstract static class Decoder {

    /**
     * 验签并解码
     * @param token JWT字符串
     * @return JWT对象
     */
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

    protected abstract JwtParser parser();
  }

  static class EcdsaDecoder extends Decoder {

    private final PublicKey key;

    private EcdsaDecoder(final PublicKey key) {
      this.key = key;
    }

    @Override
    protected JwtParser parser() {
      return Jwts.parser().verifyWith(key).build();
    }
  }

  static class HmacDecoder extends Decoder {

    private final SecretKey key;

    HmacDecoder(SecretKey key) {
      this.key = key;
    }

    @Override
    protected JwtParser parser() {
      return Jwts.parser().verifyWith(key).build();
    }
  }
}
