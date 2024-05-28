package io.github.xezzon.geom.user;

import io.github.xezzon.geom.user.repository.UserRepository;
import io.github.xezzon.tao.trait.NewType;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class UserDAO implements NewType<UserRepository> {

  private final UserRepository repository;

  public UserDAO(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserRepository get() {
    return this.repository;
  }
}
