package io.github.xezzon.geom.crypto.domain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.xezzon.geom.auth.JwtClaim;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.tao.trait.NewType;

/**
 * @author xezzon
 */
public record JwtClaimWrapper(JwtClaim value) implements
    NewType<JwtClaim>,
    Into<JWTCreator.Builder> {

  public static final String USERNAME_CLAIM = "preferred_username";
  public static final String NICKNAME_CLAIM = "nickname";
  public static final String ROLES_CLAIM = "roles";
  public static final String GROUPS_CLAIM = "groups";
  public static final String PERMISSION_CLAIM = "entitlements";

  @Override
  public JwtClaim get() {
    return this.value;
  }

  @Override
  public Builder into() {
    return JWT.create()
        .withSubject(this.get().getSubject())
        .withClaim(USERNAME_CLAIM, this.get().getPreferredUsername())
        .withClaim(NICKNAME_CLAIM, this.get().getNickname())
        .withClaim(ROLES_CLAIM, this.get().getRolesList())
        .withClaim(GROUPS_CLAIM, this.get().getGroupsList())
        .withClaim(PERMISSION_CLAIM, this.get().getEntitlementsList());
  }

  public static JwtClaimWrapper from(DecodedJWT jwt) {
    JwtClaim claim = JwtClaim.newBuilder()
        .setSubject(jwt.getSubject())
        .setPreferredUsername(jwt.getClaim(USERNAME_CLAIM).asString())
        .setNickname(jwt.getClaim(NICKNAME_CLAIM).asString())
        .addAllRoles(jwt.getClaim(ROLES_CLAIM).asList(String.class))
        .addAllEntitlements(jwt.getClaim(PERMISSION_CLAIM).asList(String.class))
        .addAllGroups(jwt.getClaim(GROUPS_CLAIM).asList(String.class))
        .build();
    return new JwtClaimWrapper(claim);
  }
}
