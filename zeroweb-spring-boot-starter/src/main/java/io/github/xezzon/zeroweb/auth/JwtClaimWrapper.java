package io.github.xezzon.zeroweb.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 * JWT包装器
 * @author xezzon
 */
@Getter
public class JwtClaimWrapper {

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

  /**
   * 认证主体ID
   */
  private final String sub;
  /**
   * 用户名
   */
  private final String preferredUsername;
  /**
   * 昵称
   */
  private final String nickname;
  /**
   * 角色
   */
  private final List<String> roles;
  /**
   * 权限
   */
  private final List<String> entitlements;
  /**
   * 令牌有效时长（秒）
   */
  private final Long exi;

  /**
   * 从JWT对象构造
   * @param decodedJWT JWT对象
   */
  public JwtClaimWrapper(DecodedJWT decodedJWT) {
    this.sub = decodedJWT.getSubject();
    this.preferredUsername = decodedJWT.getClaim(USERNAME_CLAIM).asString();
    this.nickname = decodedJWT.getClaim(NICKNAME_CLAIM).asString();
    this.roles = decodedJWT.getClaim(ROLES_CLAIM).asList(String.class);
    this.entitlements = decodedJWT.getClaim(PERMISSION_CLAIM).asList(String.class);
    this.exi = decodedJWT.getClaim(TIMEOUT_CLAIM).asLong();
  }

  /**
   * 从认证信息构造
   * @param claim 认证信息
   */
  public JwtClaimWrapper(JwtClaim claim) {
    this.sub = claim.getSub();
    this.preferredUsername = claim.getPreferredUsername();
    this.nickname = claim.getNickname();
    this.roles = claim.getRolesList();
    this.entitlements = claim.getEntitlementsList();
    this.exi = claim.getExi();
  }

  /**
   * @return 认证信息
   */
  public JwtClaim jwtClaim() {
    return JwtClaim.newBuilder()
        .setSub(this.sub)
        .setPreferredUsername(this.preferredUsername)
        .setNickname(this.nickname)
        .addAllRoles(this.roles)
        .addAllEntitlements(this.entitlements)
        .setExi(this.exi)
        .build();
  }

  /**
   * @return JWT构造器
   */
  public JWTCreator.Builder jwtBuilder() {
    return JWT.create()
        .withSubject(this.sub)
        .withClaim(USERNAME_CLAIM, this.preferredUsername)
        .withClaim(NICKNAME_CLAIM, this.nickname)
        .withClaim(ROLES_CLAIM, this.roles)
        .withClaim(GROUPS_CLAIM, Collections.emptyList())
        .withClaim(PERMISSION_CLAIM, this.entitlements)
        .withClaim(AUTHORIZED_PARTY_CLAIM, AZP_VALUE)
        .withClaim(TIMEOUT_CLAIM, this.exi)
        ;
  }
}
