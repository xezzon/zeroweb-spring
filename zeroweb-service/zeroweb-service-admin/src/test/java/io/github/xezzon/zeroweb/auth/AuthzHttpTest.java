package io.github.xezzon.zeroweb.auth;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.InitializeDataRunner;
import io.github.xezzon.zeroweb.auth.domain.RolePermission;
import io.github.xezzon.zeroweb.auth.domain.RoleUser;
import io.github.xezzon.zeroweb.auth.repository.RolePermissionRepository;
import io.github.xezzon.zeroweb.auth.repository.RoleUserRepository;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.user.domain.User;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class AuthzHttpTest {

  @Resource
  private InitializeDataRunner dataset;
  @Resource
  private WebTestClient webTestClient;
  @Resource
  private RoleUserRepository roleUserRepository;
  @Resource
  private RolePermissionRepository rolePermissionRepository;

  @Test
  void roleUser() {
    long initialSize = roleUserRepository.count();
    final List<User> userDataset = dataset.getUsers();
    final List<Role> roleDataset = dataset.getRoles();
    // 角色绑定人员
    List<RoleUser> userBindToRole = new ArrayList<>();
    String roleId;
    {
      Role role = RandomUtil.randomEle(roleDataset);
      roleId = role.getId();
      List<User> users = RandomUtil.randomEleList(userDataset, userDataset.size() - 2);
      for (User user : users) {
        RoleUser roleUser = new RoleUser();
        roleUser.setRoleId(roleId);
        roleUser.setUserId(user.getId());
        userBindToRole.add(roleUser);
      }
    }
    webTestClient.put()
        .uri("/auth/role/-/user")
        .bodyValue(userBindToRole)
        .exchange()
        .expectStatus().isOk();
    // 人员绑定角色
    String userId;
    List<RoleUser> roleBindToUser = new ArrayList<>();
    {
      User user = RandomUtil.randomEle(userDataset);
      userId = user.getId();
      List<Role> roles = RandomUtil.randomEleList(roleDataset, roleDataset.size() - 2);
      for (Role role : roles) {
        RoleUser roleUser = new RoleUser();
        roleUser.setUserId(userId);
        roleUser.setRoleId(role.getId());
        roleBindToUser.add(roleUser);
      }
    }
    webTestClient.put()
        .uri("/auth/role/-/user")
        .bodyValue(roleBindToUser)
        .exchange()
        .expectStatus().isOk();
    //
    List<RoleUser> roleUsers = Stream.concat(
        userBindToRole.stream(),
        roleBindToUser.stream()
    ).toList();
    // 读取角色绑定的人员
    {
      List<User> responseBody = webTestClient.get()
          .uri("/auth/role/{roleId}/user", roleId)
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(User.class)
          .returnResult().getResponseBody();
      Assertions.assertNotNull(responseBody);
      Collection<String> exceptUserIds = roleUsers.stream()
          .filter(o -> Objects.equals(o.getRoleId(), roleId))
          .map(RoleUser::getUserId)
          .distinct()
          .sorted()
          .toList();
      List<String> actualUserIds = responseBody.stream()
          .map(User::getId)
          .sorted()
          .toList();
      Assertions.assertIterableEquals(exceptUserIds, actualUserIds);
    }
    // 读取人员绑定角色
    {
      List<Role> responseBody = webTestClient.get()
          .uri("/auth/user/{userId}/role", userId)
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(Role.class)
          .returnResult().getResponseBody();
      Assertions.assertNotNull(responseBody);
      Collection<String> exceptUserIds = roleUsers.stream()
          .filter(o -> Objects.equals(o.getUserId(), userId))
          .map(RoleUser::getRoleId)
          .distinct()
          .sorted()
          .toList();
      List<String> actualUserIds = responseBody.stream()
          .map(Role::getId)
          .sorted()
          .toList();
      Assertions.assertIterableEquals(exceptUserIds, actualUserIds);
    }
    // 解绑角色-人员
    {
      webTestClient.method(HttpMethod.DELETE)
          .uri("/auth/role/-/user")
          .bodyValue(roleUsers)
          .exchange()
          .expectStatus().isOk();
      Assertions.assertEquals(initialSize, roleUserRepository.count());
    }
  }

  @Test
  void rolePermission() {
    long initialSize = rolePermissionRepository.count();
    final List<Role> roleDataset = dataset.getRoles();
    final List<String> permissionDataset = dataset.getPermissions();
    // 角色绑定权限
    List<RolePermission> permissionBindToRole = new ArrayList<>();
    String roleId;
    {
      Role role = RandomUtil.randomEle(roleDataset);
      roleId = role.getId();
      List<String> permissions = RandomUtil.randomEleList(
          permissionDataset, permissionDataset.size() - 2
      );
      for (String permission : permissions) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermission(permission);
        permissionBindToRole.add(rolePermission);
      }
    }
    webTestClient.put()
        .uri("/auth/role/-/permission")
        .bodyValue(permissionBindToRole)
        .exchange()
        .expectStatus().isOk();
    // 权限绑定角色
    List<RolePermission> roleBindToPermission = new ArrayList<>();
    String permission;
    {
      permission = RandomUtil.randomEle(permissionDataset);
      List<Role> roles = RandomUtil.randomEleList(roleDataset, roleDataset.size() - 2);
      for (Role role : roles) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setPermission(permission);
        rolePermission.setRoleId(role.getId());
        roleBindToPermission.add(rolePermission);
      }
    }
    webTestClient.put()
        .uri("/auth/role/-/permission")
        .bodyValue(roleBindToPermission)
        .exchange()
        .expectStatus().isOk();
    //
    List<RolePermission> rolePermissions = Stream.concat(
        permissionBindToRole.stream(),
        roleBindToPermission.stream()
    ).toList();
    // 读取角色绑定的权限
    {
      List<Object> responseBody = webTestClient.get()
          .uri("/auth/role/{roleId}/permission", roleId)
          .exchange()
          .expectBodyList(Object.class)
          .returnResult().getResponseBody();
      Assertions.assertNotNull(responseBody);
      List<String> except = rolePermissions.stream()
          .filter(o -> Objects.equals(o.getRoleId(), roleId))
          .map(RolePermission::getPermission)
          .distinct()
          .sorted()
          .toList();
      List<Object> actual = responseBody.stream()
          .sorted()
          .toList();
      Assertions.assertEquals(except, actual);
    }
    // 读取权限绑定的角色
    {
      List<Role> responseBody = webTestClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/auth/permission/-/role")
              .queryParam("permission", permission)
              .build()
          )
          .exchange()
          .expectStatus().isOk()
          .expectBodyList(Role.class)
          .returnResult().getResponseBody();
      Assertions.assertNotNull(responseBody);
      List<String> except = rolePermissions.stream()
          .filter(o -> Objects.equals(o.getPermission(), permission))
          .map(RolePermission::getRoleId)
          .distinct()
          .sorted()
          .toList();
      List<String> actual = responseBody.stream()
          .map(Role::getId)
          .sorted()
          .toList();
      Assertions.assertIterableEquals(except, actual);
    }
    // 解绑角色-权限
    webTestClient.method(HttpMethod.DELETE)
        .uri("/auth/role/-/permission")
        .bodyValue(rolePermissions)
        .exchange()
        .expectStatus().isOk();
    Assertions.assertEquals(initialSize, rolePermissionRepository.count());
  }
}
