package io.github.xezzon.zeroweb.auth;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;

import jakarta.annotation.Resource;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class JwtStpInterfaceTest {

  @Resource
  private RestTestClient testClient;

  @Test
  void checkRole() {
    testClient.get()
        .uri("/check/role")
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();
    testClient.get()
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
    testClient.get()
        .uri("/check/permission")
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();
    testClient.get()
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
