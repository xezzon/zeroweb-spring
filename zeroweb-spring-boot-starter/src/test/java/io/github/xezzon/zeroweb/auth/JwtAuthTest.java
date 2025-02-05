package io.github.xezzon.zeroweb.auth;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.google.auth.http.AuthHttpConstants.BEARER;
import static io.github.xezzon.zeroweb.auth.JwtFilter.ACCESS_KEY_HEADER;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static io.github.xezzon.zeroweb.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;

import com.auth0.jwt.JWT;
import io.github.xezzon.zeroweb.auth.entity.JwtClaimWrapper;
import io.github.xezzon.zeroweb.common.exception.CommonErrorCode;
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
    String encodedJwt = TestJwtGenerator.generateJwt(userId);
    String username = JWT.decode(encodedJwt).getClaim(JwtClaimWrapper.USERNAME_CLAIM).asString();
    String bearer = BEARER + " " + encodedJwt;
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
    CommonErrorCode errorCode = CommonErrorCode.NOT_LOGIN;
    webTestClient.get()
        .uri("/jwt")
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer())
        .exchange()
        .expectStatus().isUnauthorized()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, errorCode.code());
  }

  @Test
  void accessKey() {
    String appId = UUID.randomUUID().toString();
    String encodedJwt = TestJwtGenerator.generateJwt4App(appId);
    String username = JWT.decode(encodedJwt).getClaim(JwtClaimWrapper.USERNAME_CLAIM).asString();
    String bearer = BEARER + " " + encodedJwt;
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
