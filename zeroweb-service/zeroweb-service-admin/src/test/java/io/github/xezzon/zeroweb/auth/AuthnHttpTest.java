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
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.auth.entity.BasicAuth;
import io.github.xezzon.zeroweb.auth.entity.OidcToken;
import io.github.xezzon.zeroweb.auth.exception.InvalidPasswordException;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.crypto.internal.JwtKeyManager;
import io.github.xezzon.zeroweb.user.User;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Resource;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.GenericContainer;

/// @author xezzon
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthnHttpTest {

  private static final String BASIC_LOGIN_URI = "/auth/login/basic";
  private static final GenericContainer<?> redisContainer =
      new GenericContainer<>("redis:7-alpine");

  private final String password = RandomUtil.randomString(8);
  private final List<User> users = new ArrayList<>();
  @Resource
  private RestTestClient testClient;
  @Resource
  private UserRepository userRepository;
  @Resource
  private SaTokenConfig saTokenConfig;
  @Resource
  private JwtKeyManager keyManager;

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

  @BeforeEach
  void setUp() {
    // 用户
    for (int i = 0, cnt = 8; i < cnt; i++) {
      User user = new User();
      user.setUsername(RandomUtil.randomString(8));
      user.setNickname(RandomUtil.randomString(8));
      user.setCipher(BCrypt.hashpw(this.password));
      users.add(user);
    }
    userRepository.saveAllAndFlush(users);
  }

  @Test
  void basicLogin() {
    final String uri = BASIC_LOGIN_URI;
    // 数据准备
    User user = users.getFirst();

    BasicAuth basicAuth = new BasicAuth(user.getUsername(), password);
    OidcToken responseBody = testClient.post()
        .uri(uri)
        .body(basicAuth)
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody);
    String tokenValue0 = responseBody.getAccessToken();
    // 再次以相同用户登录，返回相同的令牌
    OidcToken responseBody1 = testClient.post()
        .uri(uri)
        .body(basicAuth)
        .header(saTokenConfig.getTokenName(), responseBody.getAccessToken())
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody1);
    assertEquals(tokenValue0, responseBody1.getAccessToken());
    // 以不同的用户登录，返回不同的令牌
    User user2 = users.get(1);
    BasicAuth basicAuth2 = new BasicAuth(user2.getUsername(), password);
    OidcToken responseBody2 = testClient.post()
        .uri(uri)
        .body(basicAuth2)
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
    User user = users.getFirst();
    // 用户名不正确
    BasicAuth basicAuth1 = new BasicAuth(RandomUtil.randomString(9), password);
    testClient.post()
        .uri(uri)
        .body(basicAuth1)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, InvalidPasswordException.ERROR_CODE);
    // 密码不正确
    BasicAuth basicAuth2 = new BasicAuth(user.getUsername(), RandomUtil.randomString(9));
    testClient.post()
        .uri(uri)
        .body(basicAuth2)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, InvalidPasswordException.ERROR_CODE);
  }

  @Test
  void self() {
    final String uri = "/auth/self";
    User user = users.getFirst();
    BasicAuth basicAuth = new BasicAuth(user.getUsername(), password);
    OidcToken responseBody = testClient.post()
        .uri(BASIC_LOGIN_URI)
        .body(basicAuth)
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody);

    Map<String, Object> responseBody1 = testClient.get()
        .uri(uri)
        .header(saTokenConfig.getTokenName(), responseBody.getAccessToken())
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<@NonNull Map<String, Object>>() {
        })
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody1);

    assertEquals(user.getId(), responseBody1.get("sub"));
  }

  @RepeatedTest(2)
  void signJwt() {
    final String uri = "/auth/token";
    User user = users.getFirst();
    BasicAuth basicAuth = new BasicAuth(user.getUsername(), password);
    OidcToken responseBody = testClient.post()
        .uri(BASIC_LOGIN_URI)
        .body(basicAuth)
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody);

    OidcToken responseBody1 = testClient.get()
        .uri(uri)
        .header(saTokenConfig.getTokenName(), responseBody.getAccessToken())
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody1);

    ECPublicKey publicKey = keyManager.getPublicKey();
    Claims payload = assertDoesNotThrow(() -> Jwts.parser()
        .verifyWith(publicKey).build()
        .parseSignedClaims(responseBody1.getIdToken())
        .getPayload()
    );
    assertNotNull(payload);
    assertEquals(user.getId(), payload.getSubject());
  }

  @Test
  void forwardAuth() {
    final ECPublicKey publicKey = keyManager.getPublicKey();
    User user = users.getFirst();
    BasicAuth basicAuth = new BasicAuth(user.getUsername(), password);
    OidcToken responseBody = testClient.post()
        .uri(BASIC_LOGIN_URI)
        .body(basicAuth)
        .exchange()
        .expectBody(OidcToken.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody);

    testClient.get()
        .uri("/auth/self")
        .header(saTokenConfig.getTokenName(), responseBody.getAccessToken())
        .exchange()
        .expectHeader().value(PUBLIC_KEY_HEADER, key ->
            assertArrayEquals(publicKey.getEncoded(), Base64.getDecoder().decode(key))
        )
        .expectHeader().value(AUTHORIZATION, bearer -> {
          String jwt = bearer.substring(BEARER.length()).trim();
          JwtParser parser = Jwts.parser().verifyWith(publicKey).build();
          Claims excepted = parser.parseSignedClaims(responseBody.getIdToken()).getPayload();
          Claims actual = parser.parseSignedClaims(jwt).getPayload();
          assertEquals(excepted.getSubject(), actual.getSubject());
        });

    testClient.get()
        .uri("/auth/token")
        .header(saTokenConfig.getTokenName(), responseBody.getAccessToken())
        .exchange()
        .expectHeader().value(PUBLIC_KEY_HEADER, key ->
            assertArrayEquals(publicKey.getEncoded(), Base64.getDecoder().decode(key))
        )
        .expectHeader().value(AUTHORIZATION, bearer -> {
          String jwt = bearer.substring(BEARER.length()).trim();
          JwtParser parser = Jwts.parser().verifyWith(publicKey).build();
          Claims excepted = parser.parseSignedClaims(responseBody.getIdToken()).getPayload();
          Claims actual = parser.parseSignedClaims(jwt).getPayload();
          assertEquals(excepted.getSubject(), actual.getSubject());
        });
  }

  @Test
  void forwardAuth_notLogin() {
    testClient.get()
        .uri("/auth/self")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);
  }
}
