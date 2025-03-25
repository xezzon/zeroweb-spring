package io.github.xezzon.zeroweb.role;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.role.entity.AddRoleReq;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class RoleHttpTest {

  @Resource
  private WebTestClient webTestClient;

  @Test
  void addRole() {
    AddRoleReq req1 = new AddRoleReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        true,
        "1"
    );
    Id responseBody1 = webTestClient.post()
        .uri("/role")
        .bodyValue(req1)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody1);
    Assertions.assertNotNull(responseBody1.id());
  }

  @Test
  void deleteRole() {
    webTestClient.delete()
        .uri(builder -> builder
            .path("/role/{id}")
            .build(RandomUtil.randomString(8))
        )
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void listAllRole() {
    webTestClient.get()
        .uri("/role")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Role.class);
  }
}
