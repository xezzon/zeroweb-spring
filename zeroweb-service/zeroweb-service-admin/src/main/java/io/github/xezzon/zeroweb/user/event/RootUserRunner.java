package io.github.xezzon.zeroweb.user.event;

import cn.dev33.satoken.secure.BCrypt;
import io.github.xezzon.zeroweb.user.User;
import io.github.xezzon.zeroweb.user.constant.UserConstant;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import jakarta.annotation.Resource;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动时新增超级管理员账号
 * @author xezzon
 */
@Component
@Order(Short.MAX_VALUE - 1)
public class RootUserRunner implements ApplicationRunner {

  @Resource
  private UserRepository userRepository;

  /**
   * ROOT 账号的密码
   */
  @Value("${zeroweb.root-password}")
  private String rootPassword;

  @Override
  public void run(@NonNull final ApplicationArguments args) {
    final User root = UserConstant.ROOT;
    final String rootUsername = UserConstant.ROOT.getUsername();
    final String cipher = BCrypt.hashpw(rootPassword);
    final User user = userRepository.findByUsername(rootUsername)
        .orElseGet(() -> {
          User newUser = new User();
          newUser.setUsername(rootUsername);
          newUser.setNickname(UserConstant.ROOT.getNickname());
          return newUser;
        });
    user.setCipher(cipher);
    userRepository.saveAndFlush(user);
    root.setId(user.getId());
  }
}
