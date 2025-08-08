package io.github.xezzon.zeroweb.third_party_app.authn;

import cn.dev33.satoken.stp.StpUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.xezzon.zeroweb.third_party_app.AccessSecret;
import io.github.xezzon.zeroweb.third_party_app.IThirdPartyAppMemberService;
import io.github.xezzon.zeroweb.third_party_app.repository.AccessSecretRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class ThirdPartyAppMemberService implements IThirdPartyAppMemberService {

  private static final String GROUP_ID_CLAIM = "groupId";
  private static final String USER_ID_CLAIM = "userId";
  private final ThirdPartyAppMemberRepository thirdPartyAppMemberRepository;
  private final AccessSecretRepository accessSecretRepository;

  public ThirdPartyAppMemberService(
      ThirdPartyAppMemberRepository thirdPartyAppMemberRepository,
      AccessSecretRepository accessSecretRepository
  ) {
    this.thirdPartyAppMemberRepository = thirdPartyAppMemberRepository;
    this.accessSecretRepository = accessSecretRepository;
  }

  /**
   * 生成邀请码。获得邀请码的用户由管理员同意后可以加入对第三方应用的管理。
   * 邀请码的形式是一个 JWT。过期时间即为邀请码有效时间。载荷中包含应用ID。最后使用应用的密钥进行签名。
   * @param appId 第三方应用ID
   * @param userId 被邀请的用户ID。如果为 null 则邀请码对所有人有效。
   * @param timeout 邀请码有效期。单位`小时`。
   * @return 邀请码
   */
  String inviteMember(String appId, @Nullable String userId, int timeout) {
    AccessSecret accessSecret = accessSecretRepository.findById(appId).orElseThrow();
    Instant current = Instant.now();
    Builder builder = JWT.create()
        .withClaim(GROUP_ID_CLAIM, appId)
        .withIssuedAt(current)
        .withExpiresAt(current.plus(timeout, ChronoUnit.HOURS));
    if (userId != null) {
      builder.withClaim(USER_ID_CLAIM, userId);
    }
    return builder.sign(Algorithm.HMAC256(accessSecret.getSecretKey()));
  }

  /**
   * 接收邀请的人，将其添加到用户组成员中。
   * @param token 邀请码
   */
  String acceptInvitation(String token) {
    DecodedJWT decodedJWT = JWT.decode(token);
    String appId = decodedJWT.getClaim(GROUP_ID_CLAIM).asString();
    AccessSecret accessSecret = accessSecretRepository.findById(appId)
        .orElseThrow();
    // 验证邀请码的有效性
    try {
      JWT.require(Algorithm.HMAC256(accessSecret.getSecretKey()))
          .build()
          .verify(decodedJWT);
    } catch (JWTVerificationException e) {
      throw new InvalidInvitationCodeException(e);
    }
    String userId = StpUtil.getLoginIdAsString();
    String invitedOne = decodedJWT.getClaim(USER_ID_CLAIM).asString();
    if (invitedOne != null && !Objects.equals(userId, invitedOne)) {
      // 如果邀请码是针对特定用户的，那么需要校验当前用户与被邀请的用户是否一致
      throw new InvalidInvitationCodeException();
    }
    // 检查用户是否已加入，若已加入则无需重复添加
    Optional<ThirdPartyAppMember> exist = thirdPartyAppMemberRepository
        .findByGroupIdAndUserId(appId, userId);
    if (exist.isPresent()) {
      return exist.get().getId();
    }
    ThirdPartyAppMember thirdPartyAppMember = new ThirdPartyAppMember();
    thirdPartyAppMember.setGroupId(appId);
    thirdPartyAppMember.setUserId(userId);
    thirdPartyAppMember.setRoleId(ThirdPartyAppMember.DEFAULT_ROLE_ID);
    thirdPartyAppMemberRepository.save(thirdPartyAppMember);
    return thirdPartyAppMember.getId();
  }
}
