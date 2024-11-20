package io.github.xezzon.geom.call;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;
import static io.github.xezzon.geom.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.GeomOpenRequestBuilder;
import io.github.xezzon.geom.auth.JwtAuth;
import io.github.xezzon.geom.auth.JwtClaim;
import io.github.xezzon.geom.common.exception.OpenErrorCode;
import io.github.xezzon.geom.subscription.domain.Subscription;
import io.github.xezzon.geom.subscription.domain.SubscriptionStatus;
import io.github.xezzon.geom.subscription.repository.SubscriptionRepository;
import io.github.xezzon.geom.third_party_app.domain.AccessSecret;
import io.github.xezzon.geom.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.geom.third_party_app.repository.AccessSecretRepository;
import io.github.xezzon.geom.third_party_app.repository.ThirdPartyAppRepository;
import jakarta.annotation.Resource;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

/**
 * @author xezzon
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class SubscriptionCallHttpTest {

  private static final String THIRD_PARTY_APP_OWNER = RandomUtil.randomString(8);
  @Resource
  private WebTestClient webTestClient;
  @Resource
  private ThirdPartyAppRepository thirdPartyAppRepository;
  @Resource
  private SubscriptionRepository subscriptionRepository;
  @Resource
  private AccessSecretRepository accessSecretRepository;

  public Tuple3<ThirdPartyApp, Subscription, AccessSecret> initData() {
    ThirdPartyApp thirdPartyApp = new ThirdPartyApp();
    thirdPartyApp.setName(RandomUtil.randomString(8));
    thirdPartyApp.setOwnerId(THIRD_PARTY_APP_OWNER);
    thirdPartyAppRepository.save(thirdPartyApp);
    Subscription subscription = new Subscription();
    subscription.setAppId(thirdPartyApp.getId());
    subscription.setOpenapiCode(RandomUtil.randomString(8));
    subscription.setStatus(SubscriptionStatus.SUBSCRIBED);
    subscriptionRepository.save(subscription);
    AccessSecret accessSecret = new AccessSecret();
    accessSecret.setId(thirdPartyApp.getId());
    accessSecret.setSecretKey(Base64.getEncoder().encodeToString(RandomUtil.randomBytes(32)));
    accessSecretRepository.save(accessSecret);
    return Tuples.of(thirdPartyApp, subscription, accessSecret);
  }

  @Test
  void validate() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    long timestamp = Instant.now().toEpochMilli();
    Tuple3<ThirdPartyApp, Subscription, AccessSecret> dataset = this.initData();
    Mac mac = Mac.getInstance(GeomOpenRequestBuilder.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(dataset.getT3().getSecretKey());
    mac.init(new SecretKeySpec(secretKey, GeomOpenRequestBuilder.DIGEST_ALGORITHM));
    mac.update(rawBody.getBytes());
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    HttpHeaders responseHeaders = webTestClient.post()
        .uri(builder -> builder
            .path("/subscription-call/validate")
            .queryParam("path", dataset.getT2().getOpenapiCode())
            .build()
        )
        .header(GeomOpenRequestBuilder.ACCESS_KEY_HEADER, dataset.getT3().getAccessKey())
        .header(GeomOpenRequestBuilder.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(GeomOpenRequestBuilder.SIGNATURE_HEADER, signature)
        .bodyValue(rawBody)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Void.class)
        .getResponseHeaders();
    String authorization = Objects.requireNonNull(responseHeaders.get(AUTHORIZATION)).get(0);
    Assertions.assertTrue(authorization.startsWith(BEARER + " "));
    String token = authorization.substring(BEARER.length() + 1);
    JwtClaim jwtClaim = new JwtAuth(
        Base64.getDecoder().decode(dataset.getT3().getAccessKey())
    ).decode(token);
    Assertions.assertEquals(dataset.getT1().getId(), jwtClaim.getSubject());
    Assertions.assertTrue(jwtClaim.getEntitlementsList()
        .contains(dataset.getT2().getOpenapiCode())
    );
  }

  @Test
  void validate_notSubscribed() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    long timestamp = Instant.now().toEpochMilli();
    Tuple3<ThirdPartyApp, Subscription, AccessSecret> dataset = this.initData();
    Mac mac = Mac.getInstance(GeomOpenRequestBuilder.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(dataset.getT3().getSecretKey());
    mac.init(new SecretKeySpec(secretKey, GeomOpenRequestBuilder.DIGEST_ALGORITHM));
    mac.update(rawBody.getBytes());
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    webTestClient.post()
        .uri(builder -> builder
            .path("/subscription-call/validate")
            .queryParam("path", RandomUtil.randomString(8))
            .build()
        )
        .header(GeomOpenRequestBuilder.ACCESS_KEY_HEADER, dataset.getT3().getAccessKey())
        .header(GeomOpenRequestBuilder.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(GeomOpenRequestBuilder.SIGNATURE_HEADER, signature)
        .bodyValue(rawBody)
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, OpenErrorCode.UNSUBSCRIBED_OPENAPI.code());
  }

  @Test
  void validate_incorrectAccessKey() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    long timestamp = Instant.now().toEpochMilli();
    Tuple3<ThirdPartyApp, Subscription, AccessSecret> dataset = this.initData();
    Mac mac = Mac.getInstance(GeomOpenRequestBuilder.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(dataset.getT3().getSecretKey());
    mac.init(new SecretKeySpec(secretKey, GeomOpenRequestBuilder.DIGEST_ALGORITHM));
    mac.update(rawBody.getBytes());
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    webTestClient.post()
        .uri(builder -> builder
            .path("/subscription-call/validate")
            .queryParam("path", dataset.getT2().getOpenapiCode())
            .build()
        )
        .header(GeomOpenRequestBuilder.ACCESS_KEY_HEADER, RandomUtil.randomString(8))
        .header(GeomOpenRequestBuilder.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(GeomOpenRequestBuilder.SIGNATURE_HEADER, signature)
        .bodyValue(rawBody)
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, OpenErrorCode.INVALID_ACCESS_KEY.code());
  }

  @Test
  void validate_incorrectSecretKey() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    long timestamp = Instant.now().toEpochMilli();
    Tuple3<ThirdPartyApp, Subscription, AccessSecret> dataset = this.initData();
    Mac mac = Mac.getInstance(GeomOpenRequestBuilder.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(RandomUtil.randomString(8));
    mac.init(new SecretKeySpec(secretKey, GeomOpenRequestBuilder.DIGEST_ALGORITHM));
    mac.update(rawBody.getBytes());
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    webTestClient.post()
        .uri(builder -> builder
            .path("/subscription-call/validate")
            .queryParam("path", dataset.getT2().getOpenapiCode())
            .build()
        )
        .header(GeomOpenRequestBuilder.ACCESS_KEY_HEADER, RandomUtil.randomString(8))
        .header(GeomOpenRequestBuilder.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(GeomOpenRequestBuilder.SIGNATURE_HEADER, signature)
        .bodyValue(rawBody)
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, OpenErrorCode.INVALID_ACCESS_KEY.code());
  }

  @Test
  void validate_timeout() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    long timestamp = Instant.now().getEpochSecond();
    Tuple3<ThirdPartyApp, Subscription, AccessSecret> dataset = this.initData();
    Mac mac = Mac.getInstance(GeomOpenRequestBuilder.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(dataset.getT3().getSecretKey());
    mac.init(new SecretKeySpec(secretKey, GeomOpenRequestBuilder.DIGEST_ALGORITHM));
    mac.update(rawBody.getBytes());
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    HttpHeaders responseHeaders = webTestClient.post()
        .uri(builder -> builder
            .path("/subscription-call/validate")
            .queryParam("path", dataset.getT2().getOpenapiCode())
            .build()
        )
        .header(GeomOpenRequestBuilder.ACCESS_KEY_HEADER, dataset.getT3().getAccessKey())
        .header(GeomOpenRequestBuilder.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(GeomOpenRequestBuilder.SIGNATURE_HEADER, signature)
        .bodyValue(rawBody)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Void.class)
        .getResponseHeaders();
    String authorization = Objects.requireNonNull(responseHeaders.get(AUTHORIZATION)).get(0);
    Assertions.assertTrue(authorization.startsWith(BEARER + " "));
    String token = authorization.substring(BEARER.length() + 1);
    JwtAuth jwtAuth = new JwtAuth(Base64.getDecoder().decode(dataset.getT3().getAccessKey()));
    Assertions.assertThrows(RuntimeException.class, () -> jwtAuth.decode(token));
  }
}
