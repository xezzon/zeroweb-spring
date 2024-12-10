package io.github.xezzon.zeroweb.call;

import com.auth0.jwt.JWTCreator.Builder;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.github.xezzon.zeroweb.auth.domain.JwtClaimWrapper;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig.ZerowebJwtConfig;
import io.github.xezzon.zeroweb.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.service.IThirdPartyAppService;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class SubscriptionCallService {

  private final IThirdPartyAppService thirdPartyAppService;
  private final ZerowebJwtConfig zerowebJwtConfig;

  public SubscriptionCallService(
      IThirdPartyAppService thirdPartyAppService,
      ZerowebConfig zerowebConfig
  ) {
    this.thirdPartyAppService = thirdPartyAppService;
    this.zerowebJwtConfig = zerowebConfig.getJwt();
  }

  protected String signJwt(String accessKey, byte[] body, String signature, Instant iat) {
    /* 校验摘要 */
    String appId = new String(
        Base64.getDecoder().decode(accessKey),
        StandardCharsets.UTF_8
    );
    thirdPartyAppService.validateSignature(appId, body, signature);
    /* 构造JWT */
    ThirdPartyApp thirdPartyApp = thirdPartyAppService.findById(appId);
    JwtClaim claim = JwtClaim.newBuilder()
        .setSubject(appId)
        .setPreferredUsername(thirdPartyApp.getId())
        .setNickname(thirdPartyApp.getName())
        .addAllEntitlements(Collections.singleton("*"))
        .build();
    Builder jwtBuilder = new JwtClaimWrapper(claim).into();
    Instant exp = iat.plusSeconds(zerowebJwtConfig.getTimeout());
    jwtBuilder
        .withIssuer(zerowebJwtConfig.getIssuer())
        .withIssuedAt(iat)
        .withExpiresAt(exp)
        .withJWTId(UUID.randomUUID().toString());
    return new JwtAuth(
        Base64.getDecoder().decode(accessKey)
    ).sign(jwtBuilder);
  }
}
