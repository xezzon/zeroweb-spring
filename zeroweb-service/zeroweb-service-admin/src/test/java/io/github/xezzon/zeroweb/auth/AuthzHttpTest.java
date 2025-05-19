package io.github.xezzon.zeroweb.auth;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.InitializeDataRunner;
import io.github.xezzon.zeroweb.auth.repository.RolePermissionRepository;
import io.github.xezzon.zeroweb.auth.repository.RoleUserRepository;
import io.github.xezzon.zeroweb.role.RoleConstant;
import io.github.xezzon.zeroweb.role.Role;
import io.github.xezzon.zeroweb.user.User;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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
  void roleUser_admin() {
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
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
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
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
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
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
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
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
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
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
          .exchange()
          .expectStatus().isOk();
      Assertions.assertEquals(initialSize, roleUserRepository.count());
    }
  }

  @Test
  void rolePermission_admin() {
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
      Set<RolePermission> temp = new HashSet<>();
      for (String permission : permissions) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(role.getParentId());
        rolePermission.setPermission(permission);
        rolePermissionRepository.save(rolePermission);
        temp.add(rolePermission);
      }
      webTestClient.put()
          .uri("/auth/role/-/permission")
          .bodyValue(permissionBindToRole)
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
          .exchange()
          .expectStatus().isOk();
      rolePermissionRepository.deleteAll(temp);
    }
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
      Set<RolePermission> temp = new HashSet<>();
      Set<String> parentIds = roles.stream()
          .map(Role::getParentId)
          .collect(Collectors.toSet());
      for (String parentId : parentIds) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(parentId);
        rolePermission.setPermission(permission);
        rolePermissionRepository.save(rolePermission);
        temp.add(rolePermission);
      }
      webTestClient.put()
          .uri("/auth/role/-/permission")
          .bodyValue(roleBindToPermission)
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
          .exchange()
          .expectStatus().isOk();
      rolePermissionRepository.deleteAll(temp);
    }
    List<RolePermission> rolePermissions = Stream.concat(
        permissionBindToRole.stream(),
        roleBindToPermission.stream()
    ).toList();
    // 读取角色绑定的权限
    {
      List<Object> responseBody = webTestClient.get()
          .uri("/auth/role/{roleId}/permission", roleId)
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
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
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
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
    {
      webTestClient.method(HttpMethod.DELETE)
          .uri("/auth/role/-/permission")
          .bodyValue(rolePermissions)
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
          .exchange()
          .expectStatus().isOk();
      Assertions.assertEquals(initialSize, rolePermissionRepository.count());
    }
  }

  @Test
  void roleUser_normal_success() {
    long initialSize = roleUserRepository.count();
    final List<User> userDataset = dataset.getUsers();
    final List<Role> roleDataset = dataset.getRoles();

    // 获取一个有父角色的角色和当前用户
    Role targetRole = roleDataset.stream()
        .filter(role -> Objects.equals(role.getParentId(), RoleConstant.ADMIN_ID))
        .findFirst()
        .orElseThrow();
    User currentUser = RandomUtil.randomEle(userDataset);

    // 设置当前用户为目标角色的上级角色成员
    RoleUser parentRoleUser = new RoleUser();
    parentRoleUser.setRoleId(targetRole.getParentId());
    parentRoleUser.setUserId(currentUser.getId());
    roleUserRepository.save(parentRoleUser);

    // 测试角色-用户绑定
    List<RoleUser> userBindToRole = new ArrayList<>();
    User targetUser = RandomUtil.randomEle(userDataset);
    RoleUser roleUser = new RoleUser();
    roleUser.setRoleId(targetRole.getId());
    roleUser.setUserId(targetUser.getId());
    userBindToRole.add(roleUser);

    webTestClient.put()
        .uri("/auth/role/-/user")
        .bodyValue(userBindToRole)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.singletonList(RoleConstant.ADMIN))
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isOk();

    // 测试查询角色绑定的用户
    List<User> responseBody = webTestClient.get()
        .uri("/auth/role/{roleId}/user", targetRole.getId())
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.singletonList(RoleConstant.ADMIN))
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(User.class)
        .returnResult().getResponseBody();

    Assertions.assertNotNull(responseBody);
    Assertions.assertTrue(
        responseBody.stream().anyMatch(user -> user.getId().equals(targetUser.getId())));

    // 测试解绑角色-用户（当前用户属于目标角色的上级角色）
    webTestClient.method(HttpMethod.DELETE)
        .uri("/auth/role/-/user")
        .bodyValue(userBindToRole)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.singletonList(RoleConstant.ADMIN))
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isOk();

    // 测试解绑角色-用户（目标用户是当前用户）
    Role currentRole = RandomUtil.randomEle(roleDataset);
    RoleUser selfRoleUser = new RoleUser();
    selfRoleUser.setRoleId(currentRole.getId());
    selfRoleUser.setUserId(currentUser.getId());
    roleUserRepository.save(selfRoleUser);

    List<RoleUser> selfUnbind = new ArrayList<>();
    selfUnbind.add(selfRoleUser);

    webTestClient.method(HttpMethod.DELETE)
        .uri("/auth/role/-/user")
        .bodyValue(selfUnbind)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.singletonList(currentRole.getValue()))
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isOk();

    // 清理测试数据
    roleUserRepository.delete(parentRoleUser);
    Assertions.assertEquals(initialSize, roleUserRepository.count());
  }

  @Test
  void roleUser_normal_failed() {
    final List<User> userDataset = dataset.getUsers();
    final List<Role> roleDataset = dataset.getRoles();

    // 获取目标角色和当前用户
    Role targetRole = RandomUtil.randomEle(roleDataset);
    User currentUser = userDataset.get(0);

    // 测试角色-用户绑定（当前用户不属于目标角色的上级角色）
    List<RoleUser> userBindToRole = new ArrayList<>();
    RoleUser roleUser = new RoleUser();
    roleUser.setRoleId(targetRole.getId());
    roleUser.setUserId(userDataset.get(1).getId());
    userBindToRole.add(roleUser);

    webTestClient.put()
        .uri("/auth/role/-/user")
        .bodyValue(userBindToRole)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.emptyList())
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden();

    // 测试查询角色绑定的用户（当前用户不属于目标角色的上级角色）
    webTestClient.get()
        .uri("/auth/role/{roleId}/user", targetRole.getId())
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.emptyList())
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden();

    // 测试解绑角色-用户（目标用户不是当前用户，且当前用户不属于目标角色的上级角色）
    webTestClient.method(HttpMethod.DELETE)
        .uri("/auth/role/-/user")
        .bodyValue(userBindToRole)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.emptyList())
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void rolePermission_normal_success() {
    long initialSize = rolePermissionRepository.count();
    final List<User> userDataset = dataset.getUsers();
    final List<Role> roleDataset = dataset.getRoles();
    final List<String> permissionDataset = dataset.getPermissions();

    // 获取一个有父角色的角色和当前用户
    Role targetRole = roleDataset.stream()
        .filter(role -> Objects.equals(role.getParentId(), RoleConstant.ADMIN_ID))
        .findFirst()
        .orElseThrow();
    User currentUser = RandomUtil.randomEle(userDataset);
    String permission = RandomUtil.randomEle(permissionDataset);

    // 设置当前用户为目标角色的上级角色成员
    RoleUser parentRoleUser = new RoleUser();
    parentRoleUser.setRoleId(targetRole.getParentId());
    parentRoleUser.setUserId(currentUser.getId());
    roleUserRepository.save(parentRoleUser);

    // 测试角色-权限绑定（当前用户属于目标角色的上级角色）
    List<RolePermission> permissionBindToRole = new ArrayList<>();
    RolePermission rolePermission = new RolePermission();
    rolePermission.setRoleId(targetRole.getId());
    rolePermission.setPermission(permission);
    permissionBindToRole.add(rolePermission);

    // 下级角色的权限不能超过上级角色
    webTestClient.put()
        .uri("/auth/role/-/permission")
        .bodyValue(permissionBindToRole)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.singletonList(RoleConstant.ADMIN))
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden();

    // 上级角色赋予对应权限
    RolePermission parentRolePermission = new RolePermission();
    parentRolePermission.setRoleId(targetRole.getParentId());
    parentRolePermission.setPermission(permission);
    rolePermissionRepository.save(parentRolePermission);

    webTestClient.put()
        .uri("/auth/role/-/permission")
        .bodyValue(permissionBindToRole)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.singletonList(RoleConstant.ADMIN))
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isOk();

    // 设置当前用户为目标角色成员
    RoleUser memberRoleUser = new RoleUser();
    memberRoleUser.setRoleId(targetRole.getId());
    memberRoleUser.setUserId(currentUser.getId());
    roleUserRepository.save(memberRoleUser);

    // 测试查询角色绑定的权限（当前用户属于目标角色）
    webTestClient.get()
        .uri("/auth/role/{roleId}/permission", targetRole.getId())
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.singletonList(targetRole.getValue()))
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isOk();

    // 测试查询角色绑定的权限（当前用户属于目标角色的上级角色）
    List<Object> responseBody1 = webTestClient.get()
        .uri("/auth/role/{roleId}/permission", targetRole.getId())
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.singletonList(RoleConstant.ADMIN))
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Object.class)
        .returnResult().getResponseBody();

    Assertions.assertNotNull(responseBody1);
    Assertions.assertTrue(responseBody1.contains(permission));

    roleUserRepository.delete(memberRoleUser);

    // 测试查询角色绑定的权限（当前用户属于目标角色的上级角色）
    List<Object> responseBody2 = webTestClient.get()
        .uri("/auth/role/{roleId}/permission", targetRole.getId())
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.singletonList(RoleConstant.ADMIN))
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Object.class)
        .returnResult().getResponseBody();

    Assertions.assertNotNull(responseBody2);
    Assertions.assertTrue(responseBody2.contains(permission));

    // 测试解绑角色-权限（当前用户属于目标角色的上级角色）
    webTestClient.method(HttpMethod.DELETE)
        .uri("/auth/role/-/permission")
        .bodyValue(permissionBindToRole)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.singletonList(RoleConstant.ADMIN))
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isOk();

    // 清理测试数据
    roleUserRepository.delete(parentRoleUser);
    rolePermissionRepository.delete(parentRolePermission);
    Assertions.assertEquals(initialSize, rolePermissionRepository.count());
  }

  @Test
  void rolePermission_normal_failed() {
    final List<User> userDataset = dataset.getUsers();
    final List<Role> roleDataset = dataset.getRoles();
    final List<String> permissionDataset = dataset.getPermissions();

    // 获取目标角色和当前用户
    Role targetRole = RandomUtil.randomEle(roleDataset);
    User currentUser = RandomUtil.randomEle(userDataset);

    // 测试角色-权限绑定（当前用户不属于目标角色的上级角色）
    List<RolePermission> permissionBindToRole = new ArrayList<>();
    RolePermission rolePermission = new RolePermission();
    rolePermission.setRoleId(targetRole.getId());
    rolePermission.setPermission(RandomUtil.randomEle(permissionDataset));
    permissionBindToRole.add(rolePermission);

    webTestClient.put()
        .uri("/auth/role/-/permission")
        .bodyValue(permissionBindToRole)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.emptyList())
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden();

    // 测试查询角色绑定的权限（当前用户既不属于目标角色，也不属于其上级角色）
    webTestClient.get()
        .uri("/auth/role/{roleId}/permission", targetRole.getId())
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.emptyList())
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden();

    // 测试解绑角色-权限（当前用户不属于目标角色的上级角色）
    webTestClient.method(HttpMethod.DELETE)
        .uri("/auth/role/-/permission")
        .bodyValue(permissionBindToRole)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder()
            .id(currentUser.getId())
            .roles(Collections.emptyList())
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden();
  }
}
