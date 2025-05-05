package io.github.xezzon.zeroweb.role;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.InitializeDataRunner;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.role.entity.AddRoleReq;
import io.github.xezzon.zeroweb.role.repository.RoleRepository;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
  @Resource
  private InitializeDataRunner dataset;

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

  @Test
  void listMyRole() {
    List<Role> roles = RandomUtil.randomEleList(dataset.getRoles(), 2);
    List<String> roleValues = roles.stream()
        .map(Role::getValue)
        .toList();
    List<Role> responseBody = webTestClient.get()
        .uri("/role/mine")
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator
            .userBuilder()
            .roles(roleValues)
            .bearer()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Role.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    List<String> excepted = roles.stream()
        .map(Role::getId)
        .collect(Collectors.toList());
    excepted.addAll(roles.stream()
        .map(Role::getChildren)
        .flatMap(Collection::stream)
        .map(Role::getId)
        .toList()
    );
    excepted.sort(String::compareTo);
    List<String> actual = responseBody.stream()
        .map(Role::getId)
        .collect(Collectors.toList());
    actual.addAll(responseBody.stream()
        .map(Role::getChildren)
        .flatMap(Collection::stream)
        .map(Role::getId)
        .toList()
    );
    actual.sort(String::compareTo);
    Assertions.assertIterableEquals(excepted, actual);
  }
}
