package io.github.xezzon.geom.user;

import io.github.xezzon.geom.common.exception.RepeatDataException;
import io.github.xezzon.geom.user.domain.User;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class UserService {

  private final UserDAO userDAO;

  public UserService(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  protected void addUser(User user) {
    /* 前置校验 */
    Optional<User> exist = userDAO.get().findByUsername(user.getUsername());
    if (exist.isPresent()) {
      throw new RepeatDataException("用户已存在");
    }
    /* 持久化 */
    userDAO.get().save(user);
  }
}
