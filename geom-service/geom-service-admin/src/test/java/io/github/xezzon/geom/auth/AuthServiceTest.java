package io.github.xezzon.geom.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import io.github.xezzon.geom.auth.domain.BasicAuth;
import io.github.xezzon.geom.common.exception.ErrorCode;
import io.github.xezzon.geom.user.domain.User;
import io.github.xezzon.geom.user.repository.UserRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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
class AuthServiceTest {

  private static final String BASIC_LOGIN_URI = "/auth/login/basic";
  private static final GenericContainer<?> redisContainer =
      new GenericContainer<>("redis:7-alpine");

  @BeforeAll
  static void beforeAll() {
    redisContainer
        .withExposedPorts(6379)
        .start();
  }

  @DynamicPropertySource
  static void redisProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.url", () -> String.format(
        "redis://%s:%s", redisContainer.getHost(), redisContainer.getMappedPort(6379)
    ));
  }

  @Resource
  private UserRepository userRepository;
  @Resource
  private WebTestClient webTestClient;
  @Resource
  private SaTokenConfig saTokenConfig;

  @Test
  void basicLogin() {
    final String uri = BASIC_LOGIN_URI;
    // 数据准备
    String password = RandomUtil.randomString(8);
    User user = new User();
    user.setUsername(RandomUtil.randomString(8));
    user.setNickname(RandomUtil.randomString(8));
    user.setCipher(BCrypt.hashpw(password));
    userRepository.saveAndFlush(user);

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
    String password2 = RandomUtil.randomString(8);
    User user2 = new User();
    user2.setUsername(RandomUtil.randomString(8));
    user2.setNickname(RandomUtil.randomString(8));
    user2.setCipher(BCrypt.hashpw(password));
    userRepository.saveAndFlush(user2);
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
    String password = RandomUtil.randomString(8);
    User user = new User();
    user.setUsername(RandomUtil.randomString(8));
    user.setNickname(RandomUtil.randomString(8));
    user.setCipher(BCrypt.hashpw(password));
    userRepository.saveAndFlush(user);
    // 用户名不正确
    BasicAuth basicAuth1 = new BasicAuth(RandomUtil.randomString(9), password);
    webTestClient.post()
        .uri(uri)
        .bodyValue(basicAuth1)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorCode.INVALID_TOKEN.code());
    // 密码不正确
    BasicAuth basicAuth2 = new BasicAuth(user.getUsername(), RandomUtil.randomString(9));
    webTestClient.post()
        .uri(uri)
        .bodyValue(basicAuth2)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorCode.INVALID_TOKEN.code());
  }
}
