package io.github.xezzon.zeroweb.auth;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.util.RandomUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.xezzon.zeroweb.InitializeDataRunner;
import io.github.xezzon.zeroweb.auth.domain.BasicAuth;
import io.github.xezzon.zeroweb.auth.domain.JwtClaimWrapper;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig;
import io.github.xezzon.zeroweb.common.exception.AdminErrorCode;
import io.github.xezzon.zeroweb.crypto.JwtKeyManager;
import io.github.xezzon.zeroweb.user.domain.User;
import jakarta.annotation.Resource;
import java.security.interfaces.ECPublicKey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
class AuthHttpTest {

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
    SaTokenInfo responseBody = webTestClient.post()
        .uri(uri)
        .bodyValue(basicAuth)
        .exchange()
        .expectBody(SaTokenInfo.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody);
    assertEquals(user.getId(), responseBody.getLoginId());
    String tokenValue0 = responseBody.getTokenValue();
    // 再次以相同用户登录，返回相同的令牌
    SaTokenInfo responseBody1 = webTestClient.post()
        .uri(uri)
        .bodyValue(basicAuth)
        .header(saTokenConfig.getTokenName(), tokenValue0)
        .exchange()
        .expectBody(SaTokenInfo.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody1);
    assertEquals(tokenValue0, responseBody1.getTokenValue());
    // 以不同的用户登录，返回不同的令牌
    String password2 = dataset.getPassword();
    User user2 = dataset.getUsers().get(1);
    BasicAuth basicAuth2 = new BasicAuth(user2.getUsername(), password2);
    SaTokenInfo responseBody2 = webTestClient.post()
        .uri(uri)
        .bodyValue(basicAuth2)
        .header(saTokenConfig.getTokenName(), tokenValue0)
        .exchange()
        .expectBody(SaTokenInfo.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody2);
    assertNotEquals(tokenValue0, responseBody2.getTokenValue());
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
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(AdminErrorCode.INVALID_TOKEN.code());
    // 密码不正确
    BasicAuth basicAuth2 = new BasicAuth(user.getUsername(), RandomUtil.randomString(9));
    webTestClient.post()
        .uri(uri)
        .bodyValue(basicAuth2)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(AdminErrorCode.INVALID_TOKEN.code());
  }

  @RepeatedTest(2)
  void signJwt() {
    final String uri = "/auth/sso";
    String password = dataset.getPassword();
    User user = dataset.getUsers().get(0);
    BasicAuth basicAuth = new BasicAuth(user.getUsername(), password);
    SaTokenInfo responseBody = webTestClient.post()
        .uri(BASIC_LOGIN_URI)
        .bodyValue(basicAuth)
        .exchange()
        .expectBody(SaTokenInfo.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody);

    SaTokenInfo responseBody1 = webTestClient.get()
        .uri(uri)
        .header(responseBody.getTokenName(), responseBody.getTokenValue())
        .exchange()
        .expectBody(SaTokenInfo.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody1);
    assertEquals(HttpHeaders.AUTHORIZATION, responseBody1.getTokenName());

    ECPublicKey publicKey = keyManager.getPublicKey();
    JWTVerifier verifier = JWT.require(Algorithm.ECDSA256(publicKey))
        .withIssuer(zerowebConfig.getJwt().getIssuer())
        .build();
    DecodedJWT jwt = assertDoesNotThrow(() -> verifier.verify(responseBody1.getTokenValue()));
    JwtClaim claim = JwtClaimWrapper.from(jwt).get();
    assertEquals(user.getId(), claim.getSubject());
  }
}
