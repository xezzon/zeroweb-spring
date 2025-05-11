package io.github.xezzon.zeroweb.auth;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;

import jakarta.annotation.Resource;
import java.util.Collections;
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
class JwtStpInterfaceTest {

  @Resource
  private WebTestClient webTestClient;

  @Test
  void checkRole() {
    webTestClient.get()
        .uri("/check/role")
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();
    webTestClient.get()
        .uri("/check/role")
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator
            .userBuilder()
            .roles(Collections.singletonList("nothing"))
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void checkPermission() {
    webTestClient.get()
        .uri("/check/permission")
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();
    webTestClient.get()
        .uri("/check/permission")
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator
            .userBuilder()
            .permissions(Collections.singletonList("nothing"))
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden();
  }
}
