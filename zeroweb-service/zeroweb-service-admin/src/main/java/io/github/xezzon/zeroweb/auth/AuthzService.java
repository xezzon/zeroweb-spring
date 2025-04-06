package io.github.xezzon.zeroweb.auth;

import io.github.xezzon.zeroweb.auth.repository.RolePermissionRepository;
import io.github.xezzon.zeroweb.auth.repository.RoleUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthzService {

  private final RoleUserRepository roleUserRepository;
  private final RolePermissionRepository rolePermissionRepository;

  public AuthzService(
      RoleUserRepository roleUserRepository,
      RolePermissionRepository rolePermissionRepository
  ) {
    this.roleUserRepository = roleUserRepository;
    this.rolePermissionRepository = rolePermissionRepository;
  }
}
