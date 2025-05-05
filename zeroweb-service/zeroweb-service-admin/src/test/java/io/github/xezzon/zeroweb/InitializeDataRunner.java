package io.github.xezzon.zeroweb;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.role.domain.Role;
import io.github.xezzon.zeroweb.role.repository.RoleRepository;
import io.github.xezzon.zeroweb.user.domain.User;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Component
public class InitializeDataRunner implements CommandLineRunner {

  @Getter
  private String password;
  @Getter
  private final List<User> users = new ArrayList<>();
  @Getter
  private final List<Role> roles = new ArrayList<>();
  @Getter
  private final List<String> permissions = new ArrayList<>();

  @Resource
  private UserRepository userRepository;
  @Resource
  private RoleRepository roleRepository;

  @Override
  public void run(String... args) {
    // 密码
    this.password = RandomUtil.randomString(8);
    // 用户
    for (int i = 0, cnt = 8; i < cnt; i++) {
      User user = new User();
      user.setUsername(RandomUtil.randomString(8));
      user.setNickname(RandomUtil.randomString(8));
      user.setCipher(BCrypt.hashpw(this.password));
      users.add(user);
    }
    userRepository.saveAllAndFlush(users);
    // 角色
    for (int i = 0, cnt = 8; i < cnt; i++) {
      Role role = new Role();
      role.setCode(RandomUtil.randomString(8));
      role.setValue(role.getCode());
      role.setName(RandomUtil.randomString(8));
      role.setInheritable(RandomUtil.randomBoolean());
      role.setParentId("1");
      role.setChildren(new ArrayList<>());
      roles.add(role);
    }
    roleRepository.saveAllAndFlush(roles);
    // 二级角色
    List<Role> inheritableRoles = roles.stream()
        .filter(Role::getInheritable)
        .toList();
    for (int i = 0, cnt = 16; i < cnt; i++) {
      Role parent = RandomUtil.randomEle(inheritableRoles);
      Role role = new Role();
      role.setCode(RandomUtil.randomString(8));
      role.setValue(parent.getCode() + "/" + role.getCode());
      role.setName(RandomUtil.randomString(8));
      role.setInheritable(RandomUtil.randomBoolean());
      role.setParentId(parent.getId());
      parent.getChildren().add(role);
      roleRepository.saveAndFlush(role);
    }
    // 权限
    for (int i = 0, cnt = 8; i < cnt; i++) {
      permissions.add(RandomUtil.randomString(8));
    }
  }
}
