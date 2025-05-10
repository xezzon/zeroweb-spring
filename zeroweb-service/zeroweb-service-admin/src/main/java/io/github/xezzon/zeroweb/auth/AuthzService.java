package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import io.github.xezzon.zeroweb.auth.domain.RolePermission;
import io.github.xezzon.zeroweb.auth.domain.RoleUser;
import io.github.xezzon.zeroweb.auth.event.UserLoginEvent;
import io.github.xezzon.zeroweb.auth.repository.RolePermissionRepository;
import io.github.xezzon.zeroweb.auth.repository.RoleUserRepository;
import io.github.xezzon.zeroweb.auth.util.SessionUtil;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.role.service.IRoleService4Auth;
import io.github.xezzon.zeroweb.user.domain.User;
import io.github.xezzon.zeroweb.user.service.IUserService4Auth;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.event.EventListener;
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
    final String roleId = roleUser.getRoleId();
    final String userId = roleUser.getUserId();
    // 当前用户的角色是该角色的上级角色，或者有对应写入权限
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_ROLE_USER)) {
      this.checkParentRole(roleId);
    }
    boolean exist = roleUserRepository.existsByRoleIdAndUserId(roleId, userId);
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
    final String roleId = roleUser.getRoleId();
    final String userId = roleUser.getUserId();
    // 当前用户的角色是该角色的上级角色，或者有对应写入权限，或者用户是自己
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_ROLE_USER)
        && !Objects.equals(StpUtil.getLoginId(), userId)
    ) {
      this.checkParentRole(roleId);
    }
    roleUserRepository.deleteByRoleIdAndUserId(roleId, userId);
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

  /**
   * 批量查询角色关联的接口权限
   * @param roleIds 角色ID集合
   * @return 接口权限集合
   */
  Set<String> queryPermissionByRole(Collection<String> roleIds) {
    List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIdIn(roleIds);
    return rolePermissions.stream()
        .map(RolePermission::getPermission)
        .collect(Collectors.toSet());
  }

  /**
   * 将接口权限绑定到角色
   * @param rolePermission 角色-接口权限绑定关系
   */
  void bindPermissionToRole(RolePermission rolePermission) {
    final String roleId = rolePermission.getRoleId();
    final String permission = rolePermission.getPermission();
    // 当前用户是该角色的上级角色，或者有对应的写入权限
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_ROLE_PERMISSION)) {
      this.checkParentRole(roleId);
    }
    // 角色的权限不能超过其上级角色
    Role parent = roleService.findParent(roleId).orElseThrow();
    List<String> parentPermissions = rolePermissionRepository
        .findByRoleIdIn(Collections.singleton(parent.getId()))
        .stream()
        .map(RolePermission::getPermission)
        .distinct()
        .toList();
    if (Boolean.FALSE.equals(SaStrategy.instance.hasElement.apply(parentPermissions, permission))) {
      throw new NotPermissionException(permission);
    }
    boolean exist = rolePermissionRepository.existsByRoleIdAndPermission(roleId, permission);
    if (exist) {
      return;
    }
    rolePermissionRepository.save(rolePermission);
  }

  /**
   * 解除角色与接口权限的关联
   * @param rolePermission 角色-接口权限关系
   */
  void releaseRolePermission(RolePermission rolePermission) {
    final String roleId = rolePermission.getRoleId();
    final String permission = rolePermission.getPermission();
    // 当前用户是该角色的上级角色，或者有对应的写入权限
    if (!StpUtil.hasPermission(PermissionConstant.AUTHZ_ROLE_PERMISSION)) {
      this.checkParentRole(roleId);
    }
    List<Role> roles = roleService.topDownList(Collections.singleton(roleId));
    List<String> roleIds = roles.stream()
        .map(Role::getId)
        .collect(Collectors.toList());
    roleIds.add(roleId);
    rolePermissionRepository.deleteByRoleIdInAndPermission(roleIds, permission);
  }

  /**
   * 查询接口权限关联的角色集合
   * @param permission 接口权限编码
   * @return 角色信息集合
   */
  List<Role> queryRoleByPermission(String permission) {
    List<RolePermission> rolePermissions = rolePermissionRepository.findByPermission(permission);
    Set<String> roleIds = rolePermissions.stream()
        .map(RolePermission::getRoleId)
        .collect(Collectors.toSet());
    return roleService.findByIdIn(roleIds);
  }

  /**
   * 用户登录后，将授权信息加载到会话中
   * @param event 用户登录事件
   */
  @EventListener
  protected void listen(UserLoginEvent event) {
    List<Role> roles = this.queryRoleByUser(event.getUser().getId());
    Set<String> roleValues = roles.stream()
        .map(Role::getValue)
        .collect(Collectors.toSet());
    SessionUtil.saveRoles(roleValues);
    Set<String> roleIds = roles.stream()
        .map(Role::getId)
        .collect(Collectors.toSet());
    Set<String> permissions = this.queryPermissionByRole(roleIds);
    SessionUtil.savePermissions(permissions);
  }

  /**
   * 校验当前用户是否有指定角色的上级角色
   * @param roleId 角色ID
   */
  void checkParentRole(String roleId) {
    Role parent = roleService.findParent(roleId).orElseThrow();
    StpUtil.checkRole(parent.getValue());
  }
}
