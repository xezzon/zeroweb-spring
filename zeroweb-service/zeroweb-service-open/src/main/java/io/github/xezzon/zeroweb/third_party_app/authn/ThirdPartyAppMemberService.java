package io.github.xezzon.zeroweb.third_party_app.authn;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.third_party_app.AccessSecret;
import io.github.xezzon.zeroweb.third_party_app.IThirdPartyAppMemberService;
import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionConstant;
import io.github.xezzon.zeroweb.third_party_app.event.ThirdPartyAppCreatedEvent;
import io.github.xezzon.zeroweb.third_party_app.repository.AccessSecretRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
   * 生成邀请码。获得邀请码的用户由管理员同意后可以加入对第三方应用的管理。 邀请码的形式是一个 JWT。过期时间即为邀请码有效时间。载荷中包含应用ID。最后使用应用的密钥进行签名。
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
    String userId = JwtAuth.getOrThrow().getSub();
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

  List<ThirdPartyAppMember> listMember(String appId) {
    return thirdPartyAppMemberRepository.findByGroupIdOrderByCreateTimeDesc(appId);
  }

  /**
   * 应用所有权转移
   * @param appId 第三方应用ID
   * @param target 转移的目标用户
   */
  @Transactional
  void moveOwnership(String appId, String target) {
    String currentUser = JwtAuth.getOrThrow().getSub();
    ThirdPartyAppMember owner = thirdPartyAppMemberRepository
        .findByGroupIdAndUserId(appId, currentUser)
        .filter(ThirdPartyAppMember::isOwner)
        .orElseThrow(() -> new DataPermissionForbiddenException(
            appId, currentUser, ThirdPartyAppPermissionConstant.MOVE_OWNERSHIP
        ));
    ThirdPartyAppMember member = thirdPartyAppMemberRepository
        .findByGroupIdAndUserId(appId, target)
        .orElseThrow(() -> new DataPermissionForbiddenException(appId, target, ""));
    owner.moveOwnership(member);
    thirdPartyAppMemberRepository.save(owner);
    thirdPartyAppMemberRepository.save(member);
  }

  @Override
  public Optional<ThirdPartyAppMember> queryMember(String groupId, String userId) {
    return thirdPartyAppMemberRepository.findByGroupIdAndUserId(groupId, userId);
  }

  /**
   * 新增应用时记录其所有者
   * @param event 新增第三方应用事件
   */
  @EventListener
  void listen(ThirdPartyAppCreatedEvent event) {
    ThirdPartyApp thirdPartyApp = event.thirdPartyApp();
    ThirdPartyAppMember member = new ThirdPartyAppMember();
    member.setGroupId(thirdPartyApp.getId());
    member.setUserId(thirdPartyApp.getOwnerId());
    member.setRoleId(ThirdPartyAppMember.OWNER_ROLE_ID);
    thirdPartyAppMemberRepository.save(member);
  }
}
