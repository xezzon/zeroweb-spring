package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.auth.domain.RolePermission;
import io.github.xezzon.zeroweb.auth.domain.RoleUser;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.role.service.IRoleService4Auth;
import io.github.xezzon.zeroweb.user.domain.User;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 授权管理
 */
@RestController
@RequestMapping("/auth")
public class AuthzController {

  private final AuthzService authzService;
  private final IRoleService4Auth roleService;

  public AuthzController(final AuthzService authzService, final IRoleService4Auth roleService) {
    this.authzService = authzService;
    this.roleService = roleService;
  }

  /**
   * 查询角色绑定的用户
   * @param roleId 角色ID
   * @return 用户信息列表
   */
  @GetMapping("/role/{roleId}/user")
  public List<User> queryUserByRole(@PathVariable final String roleId) {
    // 当前用户的角色是该角色的上级角色，或者有相应的读取权限
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_READ)) {
      authzService.checkParentRole(roleId);
    }
    return authzService.queryUserByRole(roleId);
  }

  /**
   * 将用户绑定到角色
   * @param roleUsers 角色-用户绑定关系
   */
  @PutMapping("/role/-/user")
  public void bindUserToRole(@RequestBody Collection<RoleUser> roleUsers) {
    for (RoleUser roleUser : roleUsers) {
      authzService.bindUserToRole(roleUser);
    }
  }

  /**
   * 解除用户与角色的关联
   * @param roleUsers 角色-用户关联
   */
  @DeleteMapping("/role/-/user")
  public void releaseRoleUser(@RequestBody final Collection<RoleUser> roleUsers) {
    for (final RoleUser roleUser : roleUsers) {
      authzService.releaseRoleUser(roleUser);
    }
  }

  /**
   * 查询角色的接口权限编码集合
   * @param roleId 角色ID
   * @return 接口权限编码
   */
  @GetMapping("/role/{roleId}/permission")
  public Set<String> queryPermissionByRole(@PathVariable final String roleId) {
    // 当前用户的角色是该角色或其上级角色，或者有相应的读取权限
    final Role role = roleService.findByIdIn(Collections.singleton(roleId))
        .stream()
        .findAny()
        .orElseThrow();
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_READ)
        && !StpUtil.hasRole(role.getValue())
    ) {
      authzService.checkParentRole(roleId);
    }
    return authzService.queryPermissionByRole(Collections.singleton(roleId));
  }

  /**
   * 角色授予接口权限
   * @param rolePermissions 角色-接口权限关系
   */
  @PutMapping("/role/-/permission")
  public void bindPermissionToRole(@RequestBody Collection<RolePermission> rolePermissions) {
    for (RolePermission rolePermission : rolePermissions) {
      authzService.bindPermissionToRole(rolePermission);
    }
  }

  /**
   * 解除角色与接口权限的关联
   * @param rolePermissions 角色-接口权限关系
   */
  @DeleteMapping("/role/-/permission")
  public void releaseRolePermission(@RequestBody Collection<RolePermission> rolePermissions) {
    for (RolePermission rolePermission : rolePermissions) {
      authzService.releaseRolePermission(rolePermission);
    }
  }

  /**
   * 查询用户关联的角色
   * @param userId 用户ID
   * @return 角色信息集合
   */
  @SaCheckPermission({PermissionConstant.AUTHZ_READ})
  @GetMapping("/user/{userId}/role")
  public List<Role> queryRoleByUser(@PathVariable final String userId) {
    return authzService.queryRoleByUser(userId);
  }

  /**
   * 查询接口权限关联的角色集合
   * @param permission 接口权限编码
   * @return 角色信息集合
   */
  @SaCheckPermission({PermissionConstant.AUTHZ_READ})
  @GetMapping("/permission/-/role")
  public List<Role> queryRoleByPermission(@RequestParam final String permission) {
    return authzService.queryRoleByPermission(permission);
  }
}
