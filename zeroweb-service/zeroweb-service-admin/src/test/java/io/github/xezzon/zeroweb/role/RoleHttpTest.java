package io.github.xezzon.zeroweb.role;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.role.entity.AddRoleReq;
import io.github.xezzon.zeroweb.role.repository.RoleRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

/// @author xezzon
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoleHttpTest {

  private final List<Role> roles = new ArrayList<>();
  @Resource
  private RestTestClient testClient;
  @Resource
  private RoleRepository roleRepository;

  @BeforeEach
  void setUp() {
    // 角色
    {
      Role role = new Role();
      role.setCode(RandomUtil.randomString(8));
      role.setValue(role.getCode());
      role.setName(RandomUtil.randomString(8));
      role.setInheritable(true);
      role.setParentId(RoleConstant.ADMIN_ID);
      role.setChildren(new ArrayList<>());
      roles.add(role);
    }
    for (int i = 0, cnt = 8; i < cnt; i++) {
      Role role = new Role();
      role.setCode(RandomUtil.randomString(8));
      role.setValue(role.getCode());
      role.setName(RandomUtil.randomString(8));
      role.setInheritable(RandomUtil.randomBoolean());
      role.setParentId(RoleConstant.ADMIN_ID);
      role.setChildren(new ArrayList<>());
      roles.add(role);
    }
    roleRepository.saveAllAndFlush(roles);
    // 二级角色
    List<Role> inheritableRoles = roles.stream()
        .filter(Role::getInheritable)
        .toList();
    for (int i = 0, cnt = 16; i < cnt; i++) {
      Role parent = RandomUtil.randomEle(inheritableRoles);
      Role role = new Role();
      role.setCode(RandomUtil.randomString(8));
      role.setValue(parent.getCode() + "/" + role.getCode());
      role.setName(RandomUtil.randomString(8));
      role.setInheritable(RandomUtil.randomBoolean());
      role.setParentId(parent.getId());
      parent.getChildren().add(role);
      roleRepository.saveAndFlush(role);
    }
  }

  @AfterEach
  void tearDown() {
    roleRepository.deleteAll(roles);
  }

  @Test
  void addRole() {
    AddRoleReq req1 = new AddRoleReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        true,
        RoleConstant.ADMIN_ID
    );
    Id responseBody1 = testClient.post()
        .uri("/role")
        .body(req1)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
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
    Role role = roles.getFirst();
    testClient.delete()
        .uri(builder -> builder
            .path("/role/{id}")
            .build(role.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();
    int deleteCount = 1;
    if (role.getChildren() != null) {
      deleteCount += role.getChildren().size();
    }
    Assertions.assertEquals(excepted - deleteCount, roleRepository.count());
  }

  @Test
  void listAllRole() {
    List<Role> responseBody = testClient.get()
        .uri("/role")
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<@NonNull List<Role>>() {
        })
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(3, responseBody.size());
    Assertions.assertTrue(responseBody.stream()
        .map(Role::getChildren)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .anyMatch(o -> Objects.equals(
            o.getId(),
            roles.getFirst().getId()
        ))
    );
  }

  @Test
  void listMyRole() {
    List<Role> randomRoles = RandomUtil.randomEleList(roles, 2);
    List<String> roleValues = randomRoles.stream()
        .map(Role::getValue)
        .toList();
    List<Role> responseBody = testClient.get()
        .uri("/role/mine")
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator
            .userBuilder()
            .roles(roleValues)
            .bearer()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<@NonNull List<Role>>() {
        })
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    List<String> excepted = randomRoles.stream()
        .map(Role::getId)
        .collect(Collectors.toList());
    excepted.addAll(randomRoles.stream()
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
