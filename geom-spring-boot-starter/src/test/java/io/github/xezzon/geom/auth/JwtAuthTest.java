package io.github.xezzon.geom.auth;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;
import static io.github.xezzon.geom.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static io.github.xezzon.geom.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.common.exception.ErrorCode;
import io.github.xezzon.geom.core.error.ErrorResponse;
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
    JwtClaim claim = JwtClaim.newBuilder()
        .setSubject(UUID.randomUUID().toString())
        .setPreferredUsername(RandomUtil.randomString(8))
        .setNickname(RandomUtil.randomString(8))
        .build();
    String encodedJwt = TestJwtGenerator.generateJwt(claim);
    String bearer = BEARER + " " + encodedJwt;
    String responseBody = webTestClient.get()
        .uri("/jwt")
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, bearer)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .returnResult().getResponseBody();
    Assertions.assertEquals(claim.getPreferredUsername(), responseBody);
  }

  @Test
  void notLogin() {
    ErrorCode errorCode = ErrorCode.NOT_LOGIN;
    ErrorResponse responseBody = webTestClient.get()
        .uri("/jwt")
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer())
        .exchange()
        .expectStatus().isUnauthorized()
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
