package io.github.xezzon.zeroweb.user.internal;

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.user.IUserService4Auth;
import io.github.xezzon.zeroweb.user.User;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/// @author xezzon
@Service
public class UserService implements IUserService4Auth {

  private final UserRepository userRepository;

  UserService(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /// 添加用户
  ///
  /// @param user 用户
  /// @throws RepeatDataException 如果用户名已存在，则抛出此异常
  protected void addUser(User user) {
    /* 前置校验 */
    Optional<User> exist = userRepository.findByUsername(user.getUsername());
    if (exist.isPresent()) {
      throw new RepeatDataException("`" + user.getUsername() + "`");
    }
    /* 持久化 */
    userRepository.save(user);
  }

  /// 根据用户名获取用户信息
  ///
  /// @param username 用户名
  /// @return 返回与用户名对应的用户信息，若不存在则返回null
  protected User getByUsername(@NonNull final String username) {
    return userRepository.findByUsername(username).orElse(null);
  }

  @Override
  public User getUserByUsername(final String username) {
    return this.getByUsername(username);
  }

  @Override
  public List<User> findByIdIn(final Collection<String> userIds) {
    return userRepository.findAllById(userIds);
  }
}
