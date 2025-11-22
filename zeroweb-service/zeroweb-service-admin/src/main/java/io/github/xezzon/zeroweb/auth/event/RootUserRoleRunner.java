package io.github.xezzon.zeroweb.auth.event;

import io.github.xezzon.zeroweb.auth.RoleUser;
import io.github.xezzon.zeroweb.auth.repository.RoleUserRepository;
import io.github.xezzon.zeroweb.role.constant.RoleConstant;
import io.github.xezzon.zeroweb.user.constant.UserConstant;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动时为超级管理员添加权限
 * @author xezzon
 */
@Component
@Order(Short.MAX_VALUE)
public class RootUserRoleRunner implements ApplicationRunner {

  @Resource
  private RoleUserRepository roleUserRepository;

  @Override
  public void run(@NonNull final ApplicationArguments args) {
    Optional<RoleUser> root = roleUserRepository.findByRoleIdAndUserId(
        RoleConstant.ROOT.getId(),
        UserConstant.ROOT.getId()
    );
    if (root.isEmpty()) {
      RoleUser roleUser = new RoleUser();
      roleUser.setRoleId(RoleConstant.ROOT.getId());
      roleUser.setUserId(UserConstant.ROOT.getId());
      roleUserRepository.saveAndFlush(roleUser);
    }
  }
}
