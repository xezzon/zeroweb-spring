package io.github.xezzon.zeroweb.role.internal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.role.Role;
import io.github.xezzon.zeroweb.role.entity.AddRoleReq;
import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 角色管理
///
/// @author xezzon
@RestController
@RequestMapping("/role")
public class RoleHttpEndpoint {

  private final RoleService roleService;

  public RoleHttpEndpoint(final RoleService roleService) {
    this.roleService = roleService;
  }

  /// 新增角色
  ///
  /// @param req 角色信息
  /// @return 角色ID
  @SaCheckPermission({PermissionConstant.ROLE_WRITE})
  @PostMapping()
  public Id addRole(@RequestBody final AddRoleReq req) {
    final Role role = req.into();
    roleService.addRole(role);
    return Id.of(role.getId());
  }

  /// 查询角色列表
  ///
  /// @return 角色列表（树形）
  @SaCheckPermission({PermissionConstant.ROLE_READ})
  @GetMapping()
  public List<Role> listAllRole() {
    return roleService.topDownTree(
        Collections.singleton(DatabaseConstant.ROOT_ID),
        -1
    );
  }

  /// 删除角色
  ///
  /// @param id 角色ID
  @SaCheckPermission({PermissionConstant.ROLE_WRITE})
  @DeleteMapping("/{id}")
  public void deleteRole(@PathVariable final String id) {
    roleService.deleteRole(id);
  }

  /// 查询当前登陆人的角色及它们的下一级角色
  ///
  /// @return 角色列表
  @SaCheckLogin
  @GetMapping("/mine")
  public List<Role> listMyRole() {
    return roleService.listMyRole();
  }
}
