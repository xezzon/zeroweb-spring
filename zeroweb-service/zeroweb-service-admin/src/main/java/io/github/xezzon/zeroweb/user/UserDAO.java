package io.github.xezzon.zeroweb.user;

import io.github.xezzon.tao.trait.NewType;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class UserDAO implements NewType<UserRepository> {

  private final UserRepository repository;

  UserDAO(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserRepository get() {
    return this.repository;
  }
}
