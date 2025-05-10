package io.github.xezzon.zeroweb.role;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.role.entity.AddRoleReq;
import io.github.xezzon.zeroweb.role.repository.RoleRepository;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

  private final Role role = new Role();

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private RoleRepository roleRepository;

  @BeforeEach
  void setUp() {
    role.setCode(RandomUtil.randomString(8));
    role.setValue(role.getCode());
    role.setName(RandomUtil.randomString(8));
    role.setInheritable(RandomUtil.randomBoolean());
    role.setParentId("1");
    roleRepository.save(role);
  }

  @AfterEach
  void tearDown() {
    roleRepository.delete(role);
  }

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
    long excepted = roleRepository.count();
    webTestClient.delete()
        .uri(builder -> builder
            .path("/role/{id}")
            .build(role.getId())
        )
        .exchange()
        .expectStatus().isOk();
    Assertions.assertEquals(excepted - 1, roleRepository.count());
  }

  @Test
  void listAllRole() {
    List<Role> responseBody = webTestClient.get()
        .uri("/role")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Role.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(3, responseBody.size());
    Assertions.assertTrue(responseBody.stream()
        .map(Role::getChildren)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .anyMatch(o -> Objects.equals(
            o.getId(),
            role.getId()
        ))
    );
  }
}
