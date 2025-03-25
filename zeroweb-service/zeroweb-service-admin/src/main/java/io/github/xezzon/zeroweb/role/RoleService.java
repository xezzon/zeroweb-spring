package io.github.xezzon.zeroweb.role;

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.common.exception.RoleNotInheritableException;
import io.github.xezzon.zeroweb.role.constant.RoleConstant;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.role.repository.RoleRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class RoleService {

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
}
