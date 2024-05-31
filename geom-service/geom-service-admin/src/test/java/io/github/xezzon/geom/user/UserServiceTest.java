package io.github.xezzon.geom.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import io.github.xezzon.geom.common.exception.RepeatDataException;
import io.github.xezzon.geom.user.domain.RegisterUserReq;
import io.github.xezzon.geom.user.domain.User;
import io.github.xezzon.geom.user.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author xezzon
 */
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

  @Resource
  private UserRepository repository;
  @Resource
  private UserService userService;

  @Test
  @Transactional
  void addUser() {
    RegisterUserReq req = new RegisterUserReq();
    req.setUsername(RandomUtil.randomString(8));
    req.setNickname(RandomUtil.randomString(8));
    User user = req.into();
    user.setCipher(BCrypt.hashpw(RandomUtil.randomString(8), BCrypt.gensalt()));
    userService.addUser(user);
    assertNotNull(user.getId());
    Optional<User> after = repository.findById(user.getId());
    assertTrue(after.isPresent());
  }

  @Test
  @Transactional
  void addUser_repeat() {
    // 数据准备
    User data = new User();
    data.setUsername(RandomUtil.randomString(9));
    data.setNickname(RandomUtil.randomString(9));
    data.setCipher(RandomUtil.randomString(9));
    repository.save(data);

    RegisterUserReq req = new RegisterUserReq();
    req.setUsername(data.getUsername());
    req.setNickname(RandomUtil.randomString(8));
    req.setPassword(BCrypt.hashpw(RandomUtil.randomString(8), BCrypt.gensalt()));
    assertThrows(RepeatDataException.class, () -> userService.addUser(req.into()));
  }
}
