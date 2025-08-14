package io.github.xezzon.zeroweb.auth;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.ACCESS_KEY_HEADER;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import jakarta.annotation.Resource;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class JwtAuthTest {

  @Resource
  private WebTestClient webTestClient;

  @Test
  void login() {
    String userId = UUID.randomUUID().toString();
    String username = RandomUtil.randomString(8);
    String bearer = TestJwtGenerator.userBuilder()
        .id(userId)
        .username(username)
        .bearer();
    String responseBody = webTestClient.get()
        .uri("/jwt")
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, bearer)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .returnResult().getResponseBody();
    Assertions.assertEquals(username, responseBody);
  }

  @Test
  void notLogin() {
    webTestClient.get()
        .uri("/jwt")
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isUnauthorized()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.UNAUTHENTICATED);
  }

  @Test
  void accessKey() {
    String username = RandomUtil.randomString(8);
    String bearer = TestJwtGenerator.appBuilder()
        .username(username)
        .bearer();
    String responseBody = webTestClient.get()
        .uri("/jwt")
        .header(ACCESS_KEY_HEADER, TestJwtGenerator.getSecretKey())
        .header(AUTHORIZATION, bearer)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .returnResult().getResponseBody();
    Assertions.assertEquals(username, responseBody);
  }
}
