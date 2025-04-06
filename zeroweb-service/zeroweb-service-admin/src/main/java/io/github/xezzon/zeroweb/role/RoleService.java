package io.github.xezzon.zeroweb.role;

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.common.exception.RoleNotInheritableException;
import io.github.xezzon.zeroweb.core.tree.ITreeService;
import io.github.xezzon.zeroweb.role.constant.RoleConstant;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.role.repository.RoleRepository;
import io.github.xezzon.zeroweb.role.service.IRoleService4Auth;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class RoleService implements ITreeService<Role, String>, IRoleService4Auth {

  private final RoleRepository roleRepository;

  public RoleService(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  void addRole(Role role) {
    /* 前置校验校验 */
    // 校验上级角色是否存在并允许继承
    Optional<Role> parent = roleRepository.findById(role.getParentId());
    if (parent.isEmpty()) {
      throw new RoleNotInheritableException();
    }
    if (Boolean.FALSE.equals(parent.get().getInheritable())) {
      throw new RoleNotInheritableException();
    }
    // 角色编码=上级角色编码+角色简码
    if (RoleConstant.ADMIN.equals(parent.get().getValue())) {
      role.setValue(role.getCode());
    } else {
      role.setValue(parent.get().getValue() + "/" + role.getCode());
    }
    // 校验重复
    Optional<Role> exist = roleRepository.findByValue(role.getValue());
    if (exist.isPresent()) {
      throw new RepeatDataException(role.getValue());
    }
    /* 持久化到数据库 */
    roleRepository.save(role);
  }

  void deleteRole(String id) {
    final Optional<Role> role = roleRepository.findById(id);
    if (role.isEmpty()) {
      return;
    }
    this.deleteRole(Collections.singleton(role.get()));
  }

  /**
   * 递归删除下级角色
   * @param roles 下级角色
   */
  @Transactional
  void deleteRole(Collection<Role> roles) {
    if (roles.isEmpty()) {
      return;
    }
    final Set<String> roleIds = roles.stream()
        .map(Role::getId)
        .collect(Collectors.toSet());
    roleRepository.deleteAllByIdInBatch(roleIds);
    // 递归删除下级
    final List<Role> children = this.listByParentId(roleIds);
    this.deleteRole(children);
  }

  @Override
  public List<Role> listByParentId(Collection<String> parentIds) {
    return roleRepository.findByParentIdIn(parentIds);
  }

  @Override
  public List<Role> findByIdIn(Collection<String> roleIds) {
    return roleRepository.findAllById(roleIds);
  }
}
