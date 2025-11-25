package io.github.xezzon.zeroweb.call;

import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import io.github.xezzon.zeroweb.ZerowebOpenConstant;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.openapi.Openapi;
import io.github.xezzon.zeroweb.openapi.enumeration.HttpMethod;
import io.github.xezzon.zeroweb.openapi.enumeration.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.repository.OpenapiRepository;
import io.github.xezzon.zeroweb.subscription.Subscription;
import io.github.xezzon.zeroweb.subscription.enumeration.SubscriptionStatus;
import io.github.xezzon.zeroweb.subscription.exception.UnsubscribeOpenapiException;
import io.github.xezzon.zeroweb.subscription.repository.SubscriptionRepository;
import io.github.xezzon.zeroweb.third_party_app.AccessSecret;
import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.exception.InvalidAccessKeyException;
import io.github.xezzon.zeroweb.third_party_app.repository.AccessSecretRepository;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppRepository;
import io.jsonwebtoken.Jwts.SIG;
import jakarta.annotation.Resource;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

/// @author xezzon
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SubscriptionCallHttpTest {

  private static final String SUBSCRIPTION_CALL = "/call/{openapiCode}";
  private static final String THIRD_PARTY_APP_OWNER = RandomUtil.randomString(8);
  private final Openapi openapi = new Openapi();
  private final ThirdPartyApp thirdPartyApp = new ThirdPartyApp();
  private final Subscription subscription = new Subscription();
  private final AccessSecret accessSecret = new AccessSecret();
  @Resource
  private RestTestClient testClient;
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

  @BeforeEach
  void setUp() {
    openapi.setCode(RandomUtil.randomString(8));
    openapi.setDestination("http://localhost:" + port + "/httpbin/anything/{anything}");
    openapi.setHttpMethod(RandomUtil.randomEle(HttpMethod.values()));
    openapi.setStatus(OpenapiStatus.PUBLISHED);
    openapiRepository.save(openapi);

    thirdPartyApp.setName(RandomUtil.randomString(8));
    thirdPartyApp.setOwnerId(THIRD_PARTY_APP_OWNER);
    thirdPartyAppRepository.save(thirdPartyApp);

    subscription.setAppId(thirdPartyApp.getId());
    subscription.setOpenapiCode(openapi.getCode());
    subscription.setStatus(SubscriptionStatus.SUBSCRIBED);
    subscriptionRepository.save(subscription);

    accessSecret.setId(thirdPartyApp.getId());
    accessSecret.setSecretKey(Base64.getEncoder()
        .encodeToString(SIG.HS256.key().build().getEncoded())
    );
    accessSecretRepository.save(accessSecret);
  }

  @AfterEach
  void tearDown() {
    subscriptionRepository.deleteAll();
    thirdPartyAppRepository.deleteAll();
    openapiRepository.deleteAll();
  }

  @Test
  void validate() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().toEpochMilli();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(accessSecret.getSecretKey());
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(Bytes.concat(rawBody.getBytes(), Longs.toByteArray(timestamp)));
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    String responseBody = testClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(subscription.getOpenapiCode())
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, accessSecret.getAccessKey())
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .body(rawBody)
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
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(accessSecret.getSecretKey());
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(Bytes.concat(rawBody.getBytes(), Longs.toByteArray(timestamp)));
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    testClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(RandomUtil.randomString(8))
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, accessSecret.getAccessKey())
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .body(rawBody)
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, UnsubscribeOpenapiException.ERROR_CODE);
  }

  @Test
  void validate_incorrectAccessKey() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().toEpochMilli();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(accessSecret.getSecretKey());
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(Bytes.concat(rawBody.getBytes(), Longs.toByteArray(timestamp)));
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    testClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(subscription.getOpenapiCode())
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, Base64.getEncoder()
            .encodeToString(RandomUtil.randomString(8).getBytes())
        )
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .body(rawBody)
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, InvalidAccessKeyException.ERROR_CODE);
  }

  @Test
  void validate_incorrectSecretKey() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().toEpochMilli();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(RandomUtil.randomString(8));
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(Bytes.concat(rawBody.getBytes(), Longs.toByteArray(timestamp)));
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    testClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(subscription.getOpenapiCode())
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, accessSecret.getAccessKey())
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .body(rawBody)
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, InvalidAccessKeyException.ERROR_CODE);
  }

  @Test
  void validate_mismatchSignature() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().toEpochMilli();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(accessSecret.getSecretKey());
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(Bytes.concat("tampered message".getBytes(), Longs.toByteArray(timestamp)));
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    testClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(subscription.getOpenapiCode())
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, accessSecret.getAccessKey())
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .body(rawBody)
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, InvalidAccessKeyException.ERROR_CODE);
  }

  @Test
  void validate_timeout() throws NoSuchAlgorithmException, InvalidKeyException {
    final String rawBody = "{\"id\":\"1234567890\"}";
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().getEpochSecond();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(accessSecret.getSecretKey());
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(Bytes.concat(rawBody.getBytes(), Longs.toByteArray(timestamp)));
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    testClient.post()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(subscription.getOpenapiCode())
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, accessSecret.getAccessKey())
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .body(rawBody)
        .exchange()
        .expectStatus().isUnauthorized()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.UNAUTHENTICATED);
  }

  @Test
  void validate_emptyBody() throws NoSuchAlgorithmException, InvalidKeyException {
    final String anything = RandomUtil.randomString(8);
    final String hello = RandomUtil.randomString(8);
    long timestamp = Instant.now().toEpochMilli();
    Mac mac = Mac.getInstance(ZerowebOpenConstant.DIGEST_ALGORITHM);
    byte[] secretKey = Base64.getDecoder().decode(accessSecret.getSecretKey());
    mac.init(new SecretKeySpec(secretKey, ZerowebOpenConstant.DIGEST_ALGORITHM));
    mac.update(Bytes.concat(Longs.toByteArray(timestamp)));
    String signature = Base64.getEncoder().encodeToString(mac.doFinal());

    String responseBody = testClient.get()
        .uri(builder -> builder
            .path(SUBSCRIPTION_CALL)
            .queryParam("anything", anything)
            .queryParam("hello", hello)
            .build(subscription.getOpenapiCode())
        )
        .header(ZerowebOpenConstant.ACCESS_KEY_HEADER, accessSecret.getAccessKey())
        .header(ZerowebOpenConstant.TIMESTAMP_HEADER, String.valueOf(timestamp))
        .header(ZerowebOpenConstant.SIGNATURE_HEADER, signature)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .returnResult().getResponseBody();
    Assertions.assertEquals(anything + "," + hello, responseBody);
  }
}
