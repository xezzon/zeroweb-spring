package io.github.xezzon.zeroweb.user;

import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.Nullable;

/// @author xezzon
public interface IUserService4Auth {

  /// 根据用户名获取用户信息
  ///
  /// @param username 用户名
  /// @return 用户信息
  @Nullable User getUserByUsername(String username);

  /// 根据ID批量查询用户
  ///
  /// @param userIds 用户ID集合
  /// @return 用户列表
  List<User> findByIdIn(Collection<String> userIds);
}
