package io.github.xezzon.zeroweb;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
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

  @Resource
  private UserRepository userRepository;

  @Override
  public void run(String... args) {
    // 密码
    this.password = RandomUtil.randomString(8);
    // 用户
    for (int i = 0, cnt = 2; i < cnt; i++) {
      User user = new User();
      user.setUsername(RandomUtil.randomString(8));
      user.setNickname(RandomUtil.randomString(8));
      user.setCipher(BCrypt.hashpw(this.password));
      userRepository.saveAndFlush(user);
      users.add(user);
    }
  }
}
