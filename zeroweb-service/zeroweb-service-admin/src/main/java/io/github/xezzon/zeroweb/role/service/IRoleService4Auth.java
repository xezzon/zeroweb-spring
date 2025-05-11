package io.github.xezzon.zeroweb.role.service;

import io.github.xezzon.zeroweb.role.domain.Role;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author xezzon
 */
public interface IRoleService4Auth {

  /**
   * 根据ID批量查询角色
   * @param roleIds 角色ID
   * @return 角色列表
   */
  List<Role> findByIdIn(Collection<String> roleIds);

  /**
   * 查询上级角色
   * @param roleId 角色Id
   * @return 上级角色
   */
  Optional<Role> findParent(String roleId);

  /**
   * 查询角色子级列表
   * @param initial 角色ID
   * @return 该角色的所有子级角色（包含递归子级，但不包括自身）
   */
  List<Role> topDownList(Collection<String> initial);
}
