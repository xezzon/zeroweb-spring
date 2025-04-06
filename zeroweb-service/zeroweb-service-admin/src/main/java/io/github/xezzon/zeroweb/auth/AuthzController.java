package io.github.xezzon.zeroweb.auth;

import io.github.xezzon.zeroweb.auth.domain.RolePermission;
import io.github.xezzon.zeroweb.auth.domain.RoleUser;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.user.domain.User;
import java.util.Collection;
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

  public AuthzController(AuthzService authzService) {
    this.authzService = authzService;
  }

  /**
   * 查询角色绑定的用户
   * @param roleId 角色ID
   * @return 用户信息列表
   */
  @GetMapping("/role/{roleId}/user")
  public List<User> queryUserByRole(@PathVariable String roleId) {
    throw new UnsupportedOperationException();
  }

  /**
   * 将用户绑定到角色
   * @param roleUsers 角色-用户绑定关系
   */
  @PutMapping("/role/-/user")
  public void bindUserToRole(@RequestBody Collection<RoleUser> roleUsers) {
    throw new UnsupportedOperationException();
  }

  /**
   * 解除用户与角色的关联
   * @param roleUsers 角色-用户关联
   */
  @DeleteMapping("/role/-/user")
  public void releaseRoleUser(@RequestBody Collection<RoleUser> roleUsers) {
    throw new UnsupportedOperationException();
  }

  /**
   * 查询角色的接口权限编码集合
   * @param roleId 角色ID
   * @return 接口权限编码
   */
  @GetMapping("/role/{roleId}/permission")
  public Set<String> queryPermissionByRole(@PathVariable String roleId) {
    throw new UnsupportedOperationException();
  }

  /**
   * 角色授予接口权限
   * @param rolePermissions 角色-接口权限关系
   */
  @PutMapping("/role/-/permission")
  public void bindPermissionToRole(@RequestBody Collection<RolePermission> rolePermissions) {
    throw new UnsupportedOperationException();
  }

  /**
   * 解除角色与接口权限的关联
   * @param rolePermissions 角色-接口权限关系
   */
  @DeleteMapping("/role/-/permission")
  public void releaseRolePermission(@RequestBody Collection<RolePermission> rolePermissions) {
    throw new UnsupportedOperationException();
  }

  /**
   * 查询用户关联的角色
   * @param userId 用户ID
   * @return 角色信息集合
   */
  @GetMapping("/user/{userId}/role")
  public List<Role> queryRoleByUser(@PathVariable String userId) {
    throw new UnsupportedOperationException();
  }

  /**
   * 查询接口权限关联的角色集合
   * @param permission 接口权限编码
   * @return 角色信息集合
   */
  @GetMapping("/permission/-/role")
  public List<Role> queryRoleByPermission(@RequestParam String permission) {
    throw new UnsupportedOperationException();
  }
}
