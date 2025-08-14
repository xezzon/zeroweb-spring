package io.github.xezzon.zeroweb.auth;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.BEARER;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.hutool.core.util.RandomUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.xezzon.zeroweb.InitializeDataRunner;
import io.github.xezzon.zeroweb.auth.entity.BasicAuth;
import io.github.xezzon.zeroweb.auth.entity.OidcToken;
import io.github.xezzon.zeroweb.auth.exception.InvalidPasswordException;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.crypto.internal.JwtKeyManager;
import io.github.xezzon.zeroweb.user.User;
import jakarta.annotation.Resource;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class AuthnHttpTest {

  private static final String BASIC_LOGIN_URI = "/auth/login/basic";
  private static final GenericContainer<?> redisContainer =
      new GenericContainer<>("redis:7-alpine");

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private SaTokenConfig saTokenConfig;
  @Resource
  private JwtKeyManager keyManager;
  @Resource
  private ZerowebConfig zerowebConfig;
  @Resource
  private InitializeDataRunner dataset;

  @BeforeAll
  static void beforeAll() {
    redisContainer
        .withExposedPorts(6379)
        .start();
  }

  @DynamicPropertySource
  @SuppressWarnings("unused")
  static void redisProperties(DynamicPropertyRegistry registry) {
    registry.add("REDIS_URL", () -> String.format(
        "%s:%s", redisContainer.getHost(), redisContainer.getMappedPort(6379)
    ));
  }

  @Test
  void basicLogin() {
    final String uri = BASIC_LOGIN_URI;
    // 数据准备
    String password = dataset.getPassword();
    User user = dataset.getUsers().get(0);

    BasicAuth basicAuth = new BasicAuth(user.getUsername(), password);
    OidcToken responseBody = webTestClient.post()
        .uri(uri)
        .bodyValue(basicAuth)
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody);
    String tokenValue0 = responseBody.getAccessToken();
    // 再次以相同用户登录，返回相同的令牌
    OidcToken responseBody1 = webTestClient.post()
        .uri(uri)
        .bodyValue(basicAuth)
        .header(saTokenConfig.getTokenName(), responseBody.getAccessToken())
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody1);
    assertEquals(tokenValue0, responseBody1.getAccessToken());
    // 以不同的用户登录，返回不同的令牌
    String password2 = dataset.getPassword();
    User user2 = dataset.getUsers().get(1);
    BasicAuth basicAuth2 = new BasicAuth(user2.getUsername(), password2);
    OidcToken responseBody2 = webTestClient.post()
        .uri(uri)
        .bodyValue(basicAuth2)
        .header(saTokenConfig.getTokenName(), tokenValue0)
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody2);
    assertNotEquals(tokenValue0, responseBody2.getAccessToken());
  }

  @Test
  void basicLogin_invalidToken() {
    final String uri = BASIC_LOGIN_URI;
    // 数据准备
    String password = dataset.getPassword();
    User user = dataset.getUsers().get(0);
    // 用户名不正确
    BasicAuth basicAuth1 = new BasicAuth(RandomUtil.randomString(9), password);
    webTestClient.post()
        .uri(uri)
        .bodyValue(basicAuth1)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, InvalidPasswordException.ERROR_CODE);
    // 密码不正确
    BasicAuth basicAuth2 = new BasicAuth(user.getUsername(), RandomUtil.randomString(9));
    webTestClient.post()
        .uri(uri)
        .bodyValue(basicAuth2)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, InvalidPasswordException.ERROR_CODE);
  }

  @Test
  void self() {
    final String uri = "/auth/self";
    String password = dataset.getPassword();
    User user = dataset.getUsers().get(0);
    BasicAuth basicAuth = new BasicAuth(user.getUsername(), password);
    OidcToken responseBody = webTestClient.post()
        .uri(BASIC_LOGIN_URI)
        .bodyValue(basicAuth)
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody);

    Map<String, Object> responseBody1 = webTestClient.get()
        .uri(uri)
        .header(saTokenConfig.getTokenName(), responseBody.getAccessToken())
        .exchange()
        .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
        })
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody1);

    assertEquals(user.getId(), responseBody1.get("sub"));
  }

  @RepeatedTest(2)
  void signJwt() {
    final String uri = "/auth/token";
    String password = dataset.getPassword();
    User user = dataset.getUsers().get(0);
    BasicAuth basicAuth = new BasicAuth(user.getUsername(), password);
    OidcToken responseBody = webTestClient.post()
        .uri(BASIC_LOGIN_URI)
        .bodyValue(basicAuth)
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody);

    OidcToken responseBody1 = webTestClient.get()
        .uri(uri)
        .header(saTokenConfig.getTokenName(), responseBody.getAccessToken())
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody1);

    ECPublicKey publicKey = keyManager.getPublicKey();
    JWTVerifier verifier = JWT.require(Algorithm.ECDSA256(publicKey))
        .withIssuer(zerowebConfig.getJwt().getIssuer())
        .build();
    DecodedJWT jwt = assertDoesNotThrow(() -> verifier.verify(responseBody1.getIdToken()));
    final JwtClaimWrapper claimWrapper = new JwtClaimWrapper(jwt);
    assertEquals(user.getId(), claimWrapper.getSub());
  }

  @Test
  void forwardAuth() {
    final ECPublicKey publicKey = keyManager.getPublicKey();
    String password = dataset.getPassword();
    User user = dataset.getUsers().get(0);
    BasicAuth basicAuth = new BasicAuth(user.getUsername(), password);
    OidcToken responseBody = webTestClient.post()
        .uri(BASIC_LOGIN_URI)
        .bodyValue(basicAuth)
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody);

    webTestClient.get()
        .uri("/auth/self")
        .header(saTokenConfig.getTokenName(), responseBody.getAccessToken())
        .exchange()
        .expectHeader().value(PUBLIC_KEY_HEADER, key ->
            assertArrayEquals(publicKey.getEncoded(), Base64.getDecoder().decode(key))
        )
        .expectHeader().value(AUTHORIZATION, bearer -> {
          String jwt = bearer.substring(BEARER.length()).trim();
          JWTVerifier verifier = JWT.require(Algorithm.ECDSA256(publicKey)).build();
          DecodedJWT excepted = verifier.verify(responseBody.getIdToken());
          DecodedJWT actual = verifier.verify(jwt);
          assertEquals(excepted.getSubject(), actual.getSubject());
        });

    webTestClient.get()
        .uri("/auth/token")
        .header(saTokenConfig.getTokenName(), responseBody.getAccessToken())
        .exchange()
        .expectHeader().value(PUBLIC_KEY_HEADER, key ->
            assertArrayEquals(publicKey.getEncoded(), Base64.getDecoder().decode(key))
        )
        .expectHeader().value(AUTHORIZATION, bearer -> {
          String jwt = bearer.substring(BEARER.length()).trim();
          JWTVerifier verifier = JWT.require(Algorithm.ECDSA256(publicKey)).build();
          DecodedJWT excepted = verifier.verify(responseBody.getIdToken());
          DecodedJWT actual = verifier.verify(jwt);
          assertEquals(excepted.getSubject(), actual.getSubject());
        });
  }

  @Test
  void forwardAuth_notLogin() {
    webTestClient.get()
        .uri("/auth/self")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);
  }
}
