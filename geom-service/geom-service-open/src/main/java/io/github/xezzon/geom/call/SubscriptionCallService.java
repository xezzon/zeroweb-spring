package io.github.xezzon.geom.call;

import com.auth0.jwt.JWTCreator.Builder;
import io.github.xezzon.geom.auth.JwtAuth;
import io.github.xezzon.geom.auth.JwtClaim;
import io.github.xezzon.geom.auth.domain.JwtClaimWrapper;
import io.github.xezzon.geom.common.exception.UnsubscribeOpenapiException;
import io.github.xezzon.geom.common.config.GeomConfig;
import io.github.xezzon.geom.common.config.GeomConfig.GeomJwtConfig;
import io.github.xezzon.geom.subscription.domain.Subscription;
import io.github.xezzon.geom.subscription.domain.SubscriptionStatus;
import io.github.xezzon.geom.subscription.service.ISubscriptionService4Call;
import io.github.xezzon.geom.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.geom.third_party_app.service.IThirdPartyAppService;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class SubscriptionCallService {

  private final IThirdPartyAppService thirdPartyAppService;
  private final ISubscriptionService4Call subscriptionService;
  private final GeomJwtConfig geomJwtConfig;

  public SubscriptionCallService(
      IThirdPartyAppService thirdPartyAppService,
      ISubscriptionService4Call subscriptionService,
      GeomConfig geomConfig
  ) {
    this.thirdPartyAppService = thirdPartyAppService;
    this.subscriptionService = subscriptionService;
    this.geomJwtConfig = geomConfig.getJwt();
  }

  protected String signJwt(
      String accessKey, String path, byte[] body, String signature, Instant iat
  ) {
    /* 校验摘要 */
    String appId = new String(
        Base64.getDecoder().decode(accessKey),
        StandardCharsets.UTF_8
    );
    thirdPartyAppService.validateSignature(appId, body, signature);
    /* 校验订阅 */
    List<Subscription> subscriptions = subscriptionService.listSubscriptionsOfApp(appId);
    List<String> openapiCodes = subscriptions.parallelStream()
        .filter(subscription ->
            subscription.getSubscriptionStatus() == SubscriptionStatus.SUBSCRIBED
        )
        .map(Subscription::getOpenapiCode)
        .toList();
    if (!openapiCodes.contains(path)) {
      throw new UnsubscribeOpenapiException();
    }
    /* 构造JWT */
    ThirdPartyApp thirdPartyApp = thirdPartyAppService.findById(appId);
    JwtClaim claim = JwtClaim.newBuilder()
        .setSubject(appId)
        .setPreferredUsername(thirdPartyApp.getId())
        .setNickname(thirdPartyApp.getName())
        .addAllEntitlements(openapiCodes)
        .build();
    Builder jwtBuilder = new JwtClaimWrapper(claim).into();
    Instant exp = iat.plusSeconds(geomJwtConfig.getTimeout());
    jwtBuilder
        .withIssuer(geomJwtConfig.getIssuer())
        .withIssuedAt(iat)
        .withExpiresAt(exp)
        .withJWTId(UUID.randomUUID().toString());
    return new JwtAuth(
        Base64.getDecoder().decode(accessKey)
    ).sign(jwtBuilder);
  }
}
