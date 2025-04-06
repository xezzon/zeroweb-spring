package io.github.xezzon.zeroweb.role.service;

import io.github.xezzon.zeroweb.role.domain.Role;
import java.util.Collection;
import java.util.List;

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
}
