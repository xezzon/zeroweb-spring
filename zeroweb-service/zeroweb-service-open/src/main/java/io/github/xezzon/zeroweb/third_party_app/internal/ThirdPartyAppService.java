/*
 * SPDX-FileCopyrightText: Copyright (C) 2025-2026 xezzon
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

package io.github.xezzon.zeroweb.third_party_app.internal;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import io.github.xezzon.zeroweb.ZerowebOpenConstant;
import io.github.xezzon.zeroweb.auth.JsonWebToken;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig.ZerowebJwtConfig;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.third_party_app.AccessSecret;
import io.github.xezzon.zeroweb.third_party_app.IThirdPartyAppService4Call;
import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMember;
import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMemberRepository;
import io.github.xezzon.zeroweb.third_party_app.event.ThirdPartyAppCreatedEvent;
import io.github.xezzon.zeroweb.third_party_app.exception.InvalidAccessKeyException;
import io.github.xezzon.zeroweb.third_party_app.repository.AccessSecretRepository;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppRepository;
import io.jsonwebtoken.Jwts.SIG;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

/// 第三方应用服务
///
/// 提供第三方应用的业务逻辑处理，包括应用管理、密钥管理、JWT签名等功能
///
/// @author xezzon
@Service
@Slf4j
public class ThirdPartyAppService implements IThirdPartyAppService4Call {

  private final ThirdPartyAppRepository thirdPartyAppRepository;
  private final ThirdPartyAppDAO thirdPartyAppDAO;
  private final AccessSecretRepository accessSecretRepository;
  private final ThirdPartyAppMemberRepository thirdPartyAppMemberRepository;
  private final ZerowebJwtConfig zerowebJwtConfig;
  @Resource
  private ApplicationEventPublisher eventPublisher;

  /// 依赖注入
  /// @param thirdPartyAppRepository 第三方应用的 JPA 接口
  /// @param thirdPartyAppDAO 第三方应用的数据库操作
  /// @param accessSecretRepository 访问凭据的 JPA 接口
  /// @param thirdPartyAppMemberRepository 第三方应用成员的 JPA 接口
  /// @param zerowebConfig JWT 相关设置
  public ThirdPartyAppService(
      final ThirdPartyAppRepository thirdPartyAppRepository,
      final ThirdPartyAppDAO thirdPartyAppDAO,
      final AccessSecretRepository accessSecretRepository,
      final ThirdPartyAppMemberRepository thirdPartyAppMemberRepository,
      final ZerowebConfig zerowebConfig
  ) {
    this.thirdPartyAppRepository = thirdPartyAppRepository;
    this.thirdPartyAppDAO = thirdPartyAppDAO;
    this.accessSecretRepository = accessSecretRepository;
    this.thirdPartyAppMemberRepository = thirdPartyAppMemberRepository;
    this.zerowebJwtConfig = zerowebConfig.getJwt();
  }

  /// 添加第三方应用并生成访问密钥
  ///
  /// @param thirdPartyApp 要添加的第三方应用对象
  /// @return 生成的访问密钥对象
  @Transactional()
  AccessSecret addThirdPartyApp(ThirdPartyApp thirdPartyApp) {
    thirdPartyAppRepository.save(thirdPartyApp);
    eventPublisher.publishEvent(new ThirdPartyAppCreatedEvent(thirdPartyApp));
    return this.rollAccessSecret(thirdPartyApp.getId());
  }

  /// 根据用户ID分页查询第三方应用列表
  ///
  /// @param userId 用户ID
  /// @return 分页查询结果，包含符合条件的第三方应用列表
  Page<@NonNull ThirdPartyApp> listThirdPartyAppByUser(String userId) {
    List<ThirdPartyAppMember> members = thirdPartyAppMemberRepository.findByUserId(userId);
    Set<String> appIds = members.stream()
        .map(ThirdPartyAppMember::getGroupId)
        .collect(Collectors.toSet());
    List<ThirdPartyApp> list = thirdPartyAppRepository.findByIdInOrderByCreateTimeDesc(appIds);
    return new PageImpl<>(list);
  }

  /// 分页查询第三方应用列表
  ///
  /// @param odata OData查询选项，用于指定分页和排序等条件
  /// @return 分页查询结果，包含符合条件的第三方应用列表
  Page<@NonNull ThirdPartyApp> listThirdPartyApp(ODataQueryOption odata) {
    return thirdPartyAppDAO.findAll(odata);
  }

  /// 更新密钥
  ///
  /// @param appId 应用标识
  /// @return 更新后的应用访问凭据与密钥
  AccessSecret rollAccessSecret(String appId) {
    SecretKey secretKey = SIG.HS256.key().build();
    AccessSecret accessSecret = new AccessSecret();
    accessSecret.setId(appId);
    accessSecret.setSecretKey(Base64.getEncoder()
        .encodeToString(secretKey.getEncoded())
    );
    accessSecretRepository.updateSecretKeyById(accessSecret.getId(), accessSecret.getSecretKey());
    return accessSecret;
  }

  /// 校验摘要并签发JWT
  ///
  /// 首先验证消息摘要的有效性，然后为第三方应用签发包含认证信息的JWT
  ///
  /// @param accessKey 访问密钥
  /// @param body 消息内容
  /// @param signature 消息摘要
  /// @param iat 消息签发时间
  /// @return 签发的JWT字符串
  /// @throws InvalidAccessKeyException 签名验证未通过时抛出
  @Override
  public String signJwt(String accessKey, byte[] body, String signature, Instant iat) {
    /* 校验摘要 */
    String appId = new String(
        Base64.getDecoder().decode(accessKey),
        StandardCharsets.UTF_8
    );
    this.validateSignature(appId, body, signature, iat);
    /* 构造JWT */
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findById(appId).orElseThrow();
    JwtClaim claim = JwtClaim.newBuilder()
        .setSub(appId)
        .setPreferredUsername(thirdPartyApp.getId())
        .setNickname(thirdPartyApp.getName())
        .addAllRoles(Collections.singleton("*"))
        .addAllEntitlements(Collections.singleton("*"))
        .build();
    return JsonWebToken.signer(Base64.getDecoder().decode(accessKey))
        .issuer(zerowebJwtConfig.getIssuer())
        .issuedAt(iat)
        .timeout(zerowebJwtConfig.getTimeout())
        .sign(claim);
  }

  /// 校验摘要
  ///
  /// @param appId 应用标识
  /// @param body 消息体
  /// @param signature 摘要
  /// @param iat 生成摘要的时间
  /// @throws InvalidAccessKeyException 签名校验失败
  private void validateSignature(
      final String appId,
      final byte[] body,
      final String signature,
      final Instant iat
  ) {
    final byte[] salt = Longs.toByteArray(iat.toEpochMilli());
    AccessSecret accessSecret = accessSecretRepository.findById(appId)
        .orElseThrow(InvalidAccessKeyException::new);
    try {
      Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
      byte[] secretKey = Base64.getDecoder().decode(accessSecret.getSecretKey());
      mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
      mac.update(Bytes.concat(body, salt));
      if (!Objects.equals(
          signature,
          Base64.getEncoder().encodeToString(mac.doFinal())
      )) {
        throw new InvalidAccessKeyException();
      }
    } catch (InvalidAccessKeyException e) {
      throw e;
    } catch (Exception _) {
      throw new InvalidAccessKeyException();
    }
  }
}
