package io.github.xezzon.zeroweb.auth;

import io.github.xezzon.zeroweb.auth.domain.RoleUser;
import io.github.xezzon.zeroweb.auth.repository.RolePermissionRepository;
import io.github.xezzon.zeroweb.auth.repository.RoleUserRepository;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.role.service.IRoleService4Auth;
import io.github.xezzon.zeroweb.user.domain.User;
import io.github.xezzon.zeroweb.user.service.IUserService4Auth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AuthzService {

  private final RoleUserRepository roleUserRepository;
  private final RolePermissionRepository rolePermissionRepository;
  private final IUserService4Auth userService;
  private final IRoleService4Auth roleService;

  public AuthzService(
      RoleUserRepository roleUserRepository,
      RolePermissionRepository rolePermissionRepository,
      IUserService4Auth userService,
      IRoleService4Auth roleService
  ) {
    this.roleUserRepository = roleUserRepository;
    this.rolePermissionRepository = rolePermissionRepository;
    this.userService = userService;
    this.roleService = roleService;
  }

  /**
   * 查询角色绑定的用户
   * @param roleId 角色ID
   * @return 用户信息列表
   */
  List<User> queryUserByRole(String roleId) {
    List<RoleUser> roleUsers = roleUserRepository.findByRoleId(roleId);
    Set<String> userIds = roleUsers.stream()
        .map(RoleUser::getUserId)
        .collect(Collectors.toSet());
    return userService.findByIdIn(userIds);
  }

  /**
   * 将用户绑定到角色
   * @param roleUser 用户-角色绑定关系
   */
  void bindUserToRole(RoleUser roleUser) {
    boolean exist = roleUserRepository.existsByRoleIdAndUserId(
        roleUser.getRoleId(), roleUser.getUserId()
    );
    if (exist) {
      return;
    }
    roleUserRepository.save(roleUser);
  }

  /**
   * 解除用户与角色的关联
   * @param roleUser 角色ID、用户ID
   */
  void releaseRoleUser(RoleUser roleUser) {
    roleUserRepository.deleteByRoleIdAndUserId(roleUser.getRoleId(), roleUser.getUserId());
  }

  /**
   * 查询用户关联的角色
   * @param userId 用户ID
   * @return 角色信息集合
   */
  List<Role> queryRoleByUser(String userId) {
    List<RoleUser> roleUsers = roleUserRepository.findByUserId(userId);
    Set<String> roleIds = roleUsers.stream()
        .map(RoleUser::getRoleId)
        .collect(Collectors.toSet());
    return roleService.findByIdIn(roleIds);
  }
}
