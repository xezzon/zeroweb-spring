package io.github.xezzon.geom.user;

import io.github.xezzon.geom.user.domain.User;
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
    /* 持久化 */
    userDAO.get().save(user);
  }
}
