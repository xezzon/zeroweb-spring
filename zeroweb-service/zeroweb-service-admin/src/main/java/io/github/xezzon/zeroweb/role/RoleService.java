package io.github.xezzon.zeroweb.role;

import io.github.xezzon.zeroweb.role.repository.RoleRepository;
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
}
