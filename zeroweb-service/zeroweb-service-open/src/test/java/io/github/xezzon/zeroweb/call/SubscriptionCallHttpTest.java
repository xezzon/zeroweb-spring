package io.github.xezzon.zeroweb.call;

import static io.github.xezzon.zeroweb.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.ZerowebOpenConstant;
import io.github.xezzon.zeroweb.common.exception.ErrorCode;
import io.github.xezzon.zeroweb.common.exception.OpenErrorCode;
import io.github.xezzon.zeroweb.core.error.ErrorResponse;
import io.github.xezzon.zeroweb.openapi.domain.HttpMethod;
import io.github.xezzon.zeroweb.openapi.domain.Openapi;
import io.github.xezzon.zeroweb.openapi.domain.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.repository.OpenapiRepository;
import io.github.xezzon.zeroweb.subscription.domain.Subscription;
import io.github.xezzon.zeroweb.subscription.domain.SubscriptionStatus;
import io.github.xezzon.zeroweb.subscription.repository.SubscriptionRepository;
import io.github.xezzon.zeroweb.third_party_app.domain.AccessSecret;
import io.github.xezzon.zeroweb.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.repository.AccessSecretRepository;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppRepository;
import jakarta.annotation.Resource;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

/**
 * @author xezzon
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class SubscriptionCallHttpTest {

  private static final String SUBSCRIPTION_CALL = "/call/{openapiCode}";
  private static final String THIRD_PARTY_APP_OWNER = RandomUtil.randomString(8);
  @Resource
  private WebTestClient webTestClient;
  @Resource
  private OpenapiRepository openapiRepository;
  @Resource
  private ThirdPartyAppRepository thirdPartyAppRepository;
  @Resource
  private SubscriptionRepository subscriptionRepository;
  @Resource
  private AccessSecretRepository accessSecretRepository;
  @LocalServerPort
  private int port;

  public Tuple4<Openapi, ThirdPartyApp, Subscription, AccessSecret> initData() {
    Openapi openapi = new Openapi();
    openapi.setCode(RandomUtil.randomString(8));
    openapi.setDestination("http://localhost:" + port + "/httpbin/anything/{anything}");
    openapi.setHttpMethod(RandomUtil.randomEle(HttpMethod.values()));
    openapi.setStatus(OpenapiStatus.PUBLISHED);
    openapiRepository.save(openapi);
    ThirdPartyApp thirdPartyApp = new ThirdPartyApp();
    thirdPartyApp.setName(RandomUtil.randomString(8));
    thirdPartyApp.setOwnerId(THIRD_PARTY_APP_OWNER);
    thirdPartyAppRepository.save(thirdPartyApp);
    Subscription subscription = new Subscription();
    subscription.setAppId(thirdPartyApp.getId());
    subscription.setOpenapiCode(openapi.getCode());
    subscription.setStatus(SubscriptionStatus.SUBSCRIBED);
    subscriptionRepository.save(subscription);
    AccessSecret accessSecret = new AccessSecret();
    accessSecret.setId(thirdPartyApp.getId());
    accessSecret.setSecretKey(Base64.getEncoder().encodeToString(RandomUtil.randomBytes(32)));
    accessSecretRepository.save(accessSecret);
    return Tuples.of(openapi, thirdPartyApp, subscription, accessSecret);
  }

  @Test
  void validate() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().toEpochMilli();
    Tuple4<Openapi, ThirdPartyApp, Subscription, AccessSecret> dataset = this.initData();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(dataset.getT4().getSecretKey());
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(rawBody.getBytes());
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    String responseBody = webTestClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(dataset.getT3().getOpenapiCode())
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, dataset.getT4().getAccessKey())
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .bodyValue(rawBody)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .returnResult().getResponseBody();
    Assertions.assertEquals(anything + "," + hello, responseBody);
  }

  @Test
  void validate_notSubscribed() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().toEpochMilli();
    Tuple4<Openapi, ThirdPartyApp, Subscription, AccessSecret> dataset = this.initData();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(dataset.getT4().getSecretKey());
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(rawBody.getBytes());
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    webTestClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(RandomUtil.randomString(8))
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, dataset.getT4().getAccessKey())
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .bodyValue(rawBody)
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, OpenErrorCode.UNSUBSCRIBED_OPENAPI.code());
  }

  @Test
  void validate_incorrectAccessKey() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().toEpochMilli();
    Tuple4<Openapi, ThirdPartyApp, Subscription, AccessSecret> dataset = this.initData();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(dataset.getT4().getSecretKey());
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(rawBody.getBytes());
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    webTestClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(dataset.getT3().getOpenapiCode())
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, RandomUtil.randomString(8))
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .bodyValue(rawBody)
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, OpenErrorCode.INVALID_ACCESS_KEY.code());
  }

  @Test
  void validate_incorrectSecretKey() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().toEpochMilli();
    Tuple4<Openapi, ThirdPartyApp, Subscription, AccessSecret> dataset = this.initData();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(RandomUtil.randomString(8));
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(rawBody.getBytes());
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    webTestClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(dataset.getT3().getOpenapiCode())
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, dataset.getT4().getAccessKey())
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .bodyValue(rawBody)
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, OpenErrorCode.INVALID_ACCESS_KEY.code());
  }

  @Test
  void validate_mismatchSignature() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().toEpochMilli();
    Tuple4<Openapi, ThirdPartyApp, Subscription, AccessSecret> dataset = this.initData();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(dataset.getT4().getSecretKey());
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update("tampered message".getBytes());
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    webTestClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(dataset.getT3().getOpenapiCode())
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, dataset.getT4().getAccessKey())
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .bodyValue(rawBody)
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, OpenErrorCode.INVALID_ACCESS_KEY.code());
  }

  @Test
  void validate_timeout() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().getEpochSecond();
    Tuple4<Openapi, ThirdPartyApp, Subscription, AccessSecret> dataset = this.initData();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(dataset.getT4().getSecretKey());
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(rawBody.getBytes());
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    final ErrorCode errorCode = ErrorCode.NOT_LOGIN;
    ErrorResponse responseBody = webTestClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(dataset.getT3().getOpenapiCode())
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, dataset.getT4().getAccessKey())
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .bodyValue(rawBody)
        .exchange()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code())
        .expectBody(ErrorResponse.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertNotNull(responseBody.error());
    Assertions.assertEquals(errorCode.code(), responseBody.code());
    Assertions.assertEquals(errorCode.name(), responseBody.error().getCode());
    Assertions.assertEquals(errorCode.message(), responseBody.error().getMessage());
  }
}
