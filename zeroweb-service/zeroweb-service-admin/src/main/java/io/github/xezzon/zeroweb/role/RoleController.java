package io.github.xezzon.zeroweb.role;

import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.role.entity.AddRoleReq;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理
 * @author xezzon
 */
@RestController
@RequestMapping("/role")
public class RoleController {

  private final RoleService roleService;

  public RoleController(RoleService roleService) {
    this.roleService = roleService;
  }

  /**
   * 新增角色
   * @param req 角色信息
   * @return 角色ID
   */
  @PostMapping()
  public Id addRole(@RequestBody AddRoleReq req) {
    Role role = req.into();
    roleService.addRole(role);
    return Id.of(role.getId());
  }

  /**
   * 删除角色
   * @param id 角色ID
   */
  @DeleteMapping("/{id}")
  public void deleteRole(@PathVariable String id) {
    roleService.deleteRole(id);
  }
}
