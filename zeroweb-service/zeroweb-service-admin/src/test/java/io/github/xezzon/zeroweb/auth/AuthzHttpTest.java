package io.github.xezzon.zeroweb.auth;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.auth.repository.RolePermissionRepository;
import io.github.xezzon.zeroweb.auth.repository.RoleUserRepository;
import io.github.xezzon.zeroweb.role.Role;
import io.github.xezzon.zeroweb.role.RoleConstant;
import io.github.xezzon.zeroweb.role.repository.RoleRepository;
import io.github.xezzon.zeroweb.user.User;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
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
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.RestTestClient;

/// @author xezzon
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class AuthzHttpTest {

  private final List<User> users = new ArrayList<>();
  private final List<Role> roles = new ArrayList<>();
  private final List<String> permissions = new ArrayList<>();
  @Resource
  private RestTestClient testClient;
  @Resource
  private RoleUserRepository roleUserRepository;
  @Resource
  private RolePermissionRepository rolePermissionRepository;
  @Resource
  private UserRepository userRepository;
  @Resource
  private RoleRepository roleRepository;

  @BeforeEach
  void setUp() {
    String password = RandomUtil.randomString(8);
    // 用户
    for (int i = 0, cnt = 8; i < cnt; i++) {
      User user = new User();
      user.setUsername(RandomUtil.randomString(8));
      user.setNickname(RandomUtil.randomString(8));
      user.setCipher(BCrypt.hashpw(password));
      users.add(user);
    }
    userRepository.saveAllAndFlush(users);
    // 角色
    for (int i = 0, cnt = 8; i < cnt; i++) {
      Role role = new Role();
      role.setCode(RandomUtil.randomString(8));
      role.setValue(role.getCode());
      role.setName(RandomUtil.randomString(8));
      role.setInheritable(i % 4 == 0 || RandomUtil.randomBoolean());
      role.setParentId("1");
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
    // 权限
    for (int i = 0, cnt = 8; i < cnt; i++) {
      permissions.add(RandomUtil.randomString(8));
    }
  }

  @Test
  void roleUser_admin() {
    long initialSize = roleUserRepository.count();
    // 角色绑定人员
    List<RoleUser> userBindToRole = new ArrayList<>();
    String roleId;
    {
      Role role = RandomUtil.randomEle(roles);
      roleId = role.getId();
      List<User> randomUsers = RandomUtil.randomEleList(users, users.size() - 2);
      for (User user : randomUsers) {
        RoleUser roleUser = new RoleUser();
        roleUser.setRoleId(roleId);
        roleUser.setUserId(user.getId());
        userBindToRole.add(roleUser);
      }
    }
    testClient.put()
        .uri("/auth/role/-/user")
        .body(userBindToRole)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();
    // 人员绑定角色
    String userId;
    List<RoleUser> roleBindToUser = new ArrayList<>();
    {
      User user = RandomUtil.randomEle(users);
      userId = user.getId();
      List<Role> randomRoles = RandomUtil.randomEleList(roles, roles.size() - 2);
      for (Role role : randomRoles) {
        RoleUser roleUser = new RoleUser();
        roleUser.setUserId(userId);
        roleUser.setRoleId(role.getId());
        roleBindToUser.add(roleUser);
      }
    }
    testClient.put()
        .uri("/auth/role/-/user")
        .body(roleBindToUser)
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
      List<User> responseBody = testClient.get()
          .uri("/auth/role/{roleId}/user", roleId)
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
          .exchange()
          .expectStatus().isOk()
          .expectBody(new ParameterizedTypeReference<@NonNull List<User>>() {
          })
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
      List<Role> responseBody = testClient.get()
          .uri("/auth/user/{userId}/role", userId)
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
          .exchange()
          .expectStatus().isOk()
          .expectBody(new ParameterizedTypeReference<@NonNull List<Role>>() {
          })
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
      testClient.method(HttpMethod.DELETE)
          .uri("/auth/role/-/user")
          .body(roleUsers)
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
    // 角色绑定权限
    List<RolePermission> permissionBindToRole = new ArrayList<>();
    String roleId;
    {
      Role role = RandomUtil.randomEle(roles);
      roleId = role.getId();
      List<String> randomPermissions = RandomUtil.randomEleList(
          permissions, permissions.size() - 2
      );
      for (String permission : randomPermissions) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermission(permission);
        permissionBindToRole.add(rolePermission);
      }
      Set<RolePermission> temp = new HashSet<>();
      for (String permission : randomPermissions) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(role.getParentId());
        rolePermission.setPermission(permission);
        rolePermissionRepository.save(rolePermission);
        temp.add(rolePermission);
      }
      testClient.put()
          .uri("/auth/role/-/permission")
          .body(permissionBindToRole)
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
      permission = RandomUtil.randomEle(permissions);
      List<Role> randomRoles = RandomUtil.randomEleList(roles, roles.size() - 2);
      for (Role role : randomRoles) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setPermission(permission);
        rolePermission.setRoleId(role.getId());
        roleBindToPermission.add(rolePermission);
      }
      Set<RolePermission> temp = new HashSet<>();
      Set<String> parentIds = randomRoles.stream()
          .map(Role::getParentId)
          .collect(Collectors.toSet());
      for (String parentId : parentIds) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(parentId);
        rolePermission.setPermission(permission);
        rolePermissionRepository.save(rolePermission);
        temp.add(rolePermission);
      }
      testClient.put()
          .uri("/auth/role/-/permission")
          .body(roleBindToPermission)
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
      List<Object> responseBody = testClient.get()
          .uri("/auth/role/{roleId}/permission", roleId)
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
          .exchange()
          .expectBody(new ParameterizedTypeReference<@NonNull List<Object>>() {
          })
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
      List<Role> responseBody = testClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/auth/permission/-/role")
              .queryParam("permission", permission)
              .build()
          )
          .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
          .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
          .exchange()
          .expectStatus().isOk()
          .expectBody(new ParameterizedTypeReference<@NonNull List<Role>>() {
          })
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
      testClient.method(HttpMethod.DELETE)
          .uri("/auth/role/-/permission")
          .body(rolePermissions)
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
    final List<User> userDataset = users;
    final List<Role> roleDataset = roles;

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

    testClient.put()
        .uri("/auth/role/-/user")
        .body(userBindToRole)
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
    List<User> responseBody = testClient.get()
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
        .expectBody(new ParameterizedTypeReference<@NonNull List<User>>() {
        })
        .returnResult().getResponseBody();

    Assertions.assertNotNull(responseBody);
    Assertions.assertTrue(
        responseBody.stream().anyMatch(user -> user.getId().equals(targetUser.getId())));

    // 测试解绑角色-用户（当前用户属于目标角色的上级角色）
    testClient.method(HttpMethod.DELETE)
        .uri("/auth/role/-/user")
        .body(userBindToRole)
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

    testClient.method(HttpMethod.DELETE)
        .uri("/auth/role/-/user")
        .body(selfUnbind)
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
    // 获取目标角色和当前用户
    Role targetRole = RandomUtil.randomEle(roles);
    User currentUser = users.getFirst();

    // 测试角色-用户绑定（当前用户不属于目标角色的上级角色）
    List<RoleUser> userBindToRole = new ArrayList<>();
    RoleUser roleUser = new RoleUser();
    roleUser.setRoleId(targetRole.getId());
    roleUser.setUserId(users.get(1).getId());
    userBindToRole.add(roleUser);

    testClient.put()
        .uri("/auth/role/-/user")
        .body(userBindToRole)
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
    testClient.get()
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
    testClient.method(HttpMethod.DELETE)
        .uri("/auth/role/-/user")
        .body(userBindToRole)
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

    // 获取一个有父角色的角色和当前用户
    Role targetRole = roles.stream()
        .filter(role -> Objects.equals(role.getParentId(), RoleConstant.ADMIN_ID))
        .findFirst()
        .orElseThrow();
    User currentUser = RandomUtil.randomEle(users);
    String permission = RandomUtil.randomEle(permissions);

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
    testClient.put()
        .uri("/auth/role/-/permission")
        .body(permissionBindToRole)
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

    testClient.put()
        .uri("/auth/role/-/permission")
        .body(permissionBindToRole)
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
    testClient.get()
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
    List<Object> responseBody1 = testClient.get()
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
        .expectBody(new ParameterizedTypeReference<@NonNull List<Object>>() {
        })
        .returnResult().getResponseBody();

    Assertions.assertNotNull(responseBody1);
    Assertions.assertTrue(responseBody1.contains(permission));

    roleUserRepository.delete(memberRoleUser);

    // 测试查询角色绑定的权限（当前用户属于目标角色的上级角色）
    List<Object> responseBody2 = testClient.get()
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
        .expectBody(new ParameterizedTypeReference<@NonNull List<Object>>() {
        })
        .returnResult().getResponseBody();

    Assertions.assertNotNull(responseBody2);
    Assertions.assertTrue(responseBody2.contains(permission));

    // 测试解绑角色-权限（当前用户属于目标角色的上级角色）
    testClient.method(HttpMethod.DELETE)
        .uri("/auth/role/-/permission")
        .body(permissionBindToRole)
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
    // 获取目标角色和当前用户
    Role targetRole = RandomUtil.randomEle(roles);
    User currentUser = RandomUtil.randomEle(users);

    // 测试角色-权限绑定（当前用户不属于目标角色的上级角色）
    List<RolePermission> permissionBindToRole = new ArrayList<>();
    RolePermission rolePermission = new RolePermission();
    rolePermission.setRoleId(targetRole.getId());
    rolePermission.setPermission(RandomUtil.randomEle(permissions));
    permissionBindToRole.add(rolePermission);

    testClient.put()
        .uri("/auth/role/-/permission")
        .body(permissionBindToRole)
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
    testClient.get()
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
    testClient.method(HttpMethod.DELETE)
        .uri("/auth/role/-/permission")
        .body(permissionBindToRole)
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
